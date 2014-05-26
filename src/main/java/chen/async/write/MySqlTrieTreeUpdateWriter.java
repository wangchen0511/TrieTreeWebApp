package chen.async.write;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySqlTrieTreeUpdateWriter implements IWriter {

	private final Connection conn;
	private final PreparedStatement stat;
	private String sql = "update trietree set count=?, isValid=? where id=?;";
	private static Logger log = LoggerFactory.getLogger(MySqlTrieTreeInsertWriter.class);
	
	public MySqlTrieTreeUpdateWriter(final Connection conn) throws SQLException{
		if(conn == null){
			throw new IllegalArgumentException("Mysql Connection can not be Null");
		}
		this.conn = conn;
		stat = conn.prepareStatement(sql);
	}
	
	@Override
	public boolean write(IDataItem data){
		if(!(data instanceof TrieTreeSQLDataItem)){
			log.warn("Incompatible DataItem Class, Expect TrieTreeSQLDataItem, but get {}", data.getClass());
			return false;
		}
		TrieTreeSQLDataItem trieDataItem = (TrieTreeSQLDataItem) data;
		TrieTreeWriteEntry trieData = (TrieTreeWriteEntry) trieDataItem.getData();
		log.debug("update {}", trieData);
		try {
			stat.clearParameters();
			stat.setLong(1, trieData.getCount());
			stat.setBoolean(2, trieData.getIsValid());
			stat.setLong(3, trieData.getId());
			stat.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void close(){
		try {
			stat.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


}
