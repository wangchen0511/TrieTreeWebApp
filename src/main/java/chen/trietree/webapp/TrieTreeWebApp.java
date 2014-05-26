package chen.trietree.webapp;



import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chen.trie.ITrieTree;
import chen.trie.ResultEntry;
import chen.trie.TreeDBWrapper;

public class TrieTreeWebApp extends HttpServlet{
	
	private static Logger log = LoggerFactory.getLogger(TrieTreeWebApp.class);
	private static final long serialVersionUID = 1L;
	private static ITrieTree trie;
	
	static{
		try {
			trie = new TreeDBWrapper(TreeDBWrapper.loadTrieFromDB("test", "jdbc:mysql://localhost", "trietree"));
		} catch (SQLException e) {
			throw new RuntimeException("Can not load the trie tree from DB.");
		}
	}

	
	/**
	 * RequestedStr=
	 * PutStr=
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//increaseCounter(1);
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.println("<html><body>");
		String queryStr = req.getQueryString();
		if(queryStr == null || (!queryStr.contains("RequestedStr") && !queryStr.contains("PutStr"))){
			out.println("Total Number of Node!" + queryStr);
		}else{
			String[] pairs = queryStr.split("&");
			for(String entry : pairs){
				String[] keyValue = entry.split("=");
				if(keyValue != null && keyValue[0].equals("RequestedStr") && keyValue[1] != null){
					for(ResultEntry str : trie.matchedStr(keyValue[1])){
						out.println("<p>" + str.getStr() + " total count is " + str.getCounter() + "</p>");
					}
				}else if (keyValue != null && keyValue[0].equals("PutStr") && keyValue[1] != null){
					trie.putStr(keyValue[1]);
				}
			}
		}
		out.println("</body></html>");
		out.close();
		resp.setStatus(HttpServletResponse.SC_OK);
	}
	
}

