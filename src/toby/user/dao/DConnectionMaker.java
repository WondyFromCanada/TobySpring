package toby.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;

/* 의존 object */
public class DConnectionMaker implements ConnectionMaker{

	@Override
	public Connection makeConnection() throws Exception {
		// D의 독자적인 Connection 생성
		Class.forName("com.mysql.jdbc.Driver");
		Connection c = DriverManager.getConnection("jdbc:mysql://localhost/toby?serverTimezone=UTC", "root", "1234");
		
		return c;
	}

}
