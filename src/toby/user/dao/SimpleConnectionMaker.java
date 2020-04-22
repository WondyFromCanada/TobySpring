package toby.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class SimpleConnectionMaker {
	public Connection makeNewConnection() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		Connection c = DriverManager.getConnection("jdbc:mysql://localhost/toby?serverTimezone=UTC", "root", "1234");
		
		return c;
	}
}
