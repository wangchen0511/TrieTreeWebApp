package chen.trie;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chen.async.write.AsyncWriterCommand;
import chen.async.write.CommandExecutor;
import chen.async.write.CreateConnection;
import chen.async.write.IWriter;
import chen.async.write.MySqlTrieTreeInsertWriter;
import chen.async.write.MySqlTrieTreeUpdateWriter;
import chen.async.write.TrieTreeSQLDataItem;
import chen.async.write.TrieTreeWriteEntry;


/**
 * Decorate of TrieTree, which will also update the disk while query a string.
 * @author adam701
 *
 */
public class TreeDBWrapper implements ITrieTree {

	private final IWriter dBInsertwriter;
	private final IWriter dBUpdatewriter;
	private final TrieTree tree;
	private final CommandExecutor dBWriter;
	private static Logger log = LoggerFactory.getLogger(TreeDBWrapper.class);

	public TreeDBWrapper(final TrieTree tree) throws SQLException {
		this.dBInsertwriter = new MySqlTrieTreeInsertWriter(CreateConnection.createConnection());
		this.dBUpdatewriter = new MySqlTrieTreeUpdateWriter(CreateConnection.createConnection());
		this.tree = tree;
		dBWriter = new CommandExecutor();
	}

	public void close() {
		dBInsertwriter.close();
		dBUpdatewriter.close();
		dBWriter.close();
	}

	@Override
	public boolean isExist(String str, boolean addCount, boolean setVaildWord) {
		return tree.isExist(str, addCount, setVaildWord);
	}

	@Override
	public List<ResultEntry> matchedStr(String str) {
		return tree.matchedStr(str);
	}

	@Override
	public TrieNode putStr(String str) {
		str = str.trim();
		TrieNode currentNode = tree.getRoot();
		TrieNode prevNode = null;
		int index = 0;
		tree.lock.writeLock().lock();
		try {
			for (; index < str.length(); index++) {
				prevNode = currentNode;
				currentNode = prevNode.getChild(str.charAt(index));
				if (currentNode == null) {
					currentNode = new TrieNode(str.charAt(index), false,
							tree.getIdAndIncrement(), prevNode.getID());
					if (index == str.length() - 1) {
						currentNode.setValidEnd(true);
						currentNode.increaseCount();
					}
					prevNode.putNewChild(str.charAt(index), currentNode);
					TrieTreeSQLDataItem dataItem = new TrieTreeSQLDataItem(
							new TrieTreeWriteEntry(currentNode.getID(),
									currentNode.getParent(), String
											.valueOf(currentNode.getChar()),
									currentNode.getCount(), currentNode
											.isValidEnd()));
					dBWriter.addCmd(new AsyncWriterCommand(dBInsertwriter, dataItem));
					log.debug("Insert {} to DB", dataItem.getData());
				} else if(index == str.length() - 1){
					currentNode.setValidEnd(true);
					currentNode.increaseCount();
					TrieTreeSQLDataItem dataItem = new TrieTreeSQLDataItem(
							new TrieTreeWriteEntry(currentNode.getID(),
									currentNode.getParent(), String
											.valueOf(currentNode.getChar()),
									currentNode.getCount(), currentNode
											.isValidEnd()));
					dBWriter.addCmd(new AsyncWriterCommand(dBUpdatewriter, dataItem));
					log.debug("Update {} to DB", dataItem.getData());
				}
			}
		} finally {
			tree.lock.writeLock().unlock();
		}
		return currentNode;
	}
	
	
	/**TODO: Later we can use multi-thread to load the results from DB.
	 *  
	 * @param dbName
	 * @param dbUrl
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public static TrieTree loadTrieFromDB(String dbName, String dbUrl, String tableName) throws SQLException{
		Map<Long, TrieNode> map = new HashMap<Long, TrieNode>();
		TrieNode root = new TrieNode('.', false, 0, 0);
		map.put(0L, root);
		Connection conn = CreateConnection.createConnection(dbUrl, dbName);
		if(conn == null){
			log.error("Fail to connect to url {} with database {}", dbUrl, dbName);
		}
		Statement stmt = conn.createStatement();
		String sql = "select * from " + tableName + " ORDER BY id;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			long id = rs.getLong("id");
			long count = rs.getLong("count");
			long parentId = rs.getLong("parentId");
			boolean isValid = rs.getBoolean("isValid");
			String ch = rs.getString("ch");
			log.debug("Load item: Id: {} parentId: {} count: {} char: {} isValid: {}", id, parentId, ch, count, isValid);
			TrieNode newNode = new TrieNode(ch.charAt(0), isValid, id, parentId);
			newNode.setCount(count);
			map.get(parentId).putNewChild(ch.charAt(0), newNode);
			map.put(id, newNode);
		}
		if(rs != null){
			rs.close();
		}
		if(stmt != null){
			stmt.close();
		}
		conn.close();
		return new TrieTree(root, map.size() + 1);
	}

}
