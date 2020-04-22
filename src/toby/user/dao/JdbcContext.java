package toby.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.datasource.SimpleDriverDataSource;

public class JdbcContext {
	private SimpleDriverDataSource dataSource;
	
	public void setDataSource(SimpleDriverDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void executeSql(final String query) throws Exception {
		workWithStatementStrategy(
			new StatementStrategy() {

				@Override
				public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
					return c.prepareStatement(query);
				}
				
			}
		);
	}

	private void workWithStatementStrategy(StatementStrategy stmt) throws Exception {
		Connection c = null;
		PreparedStatement ps = null;
		
		try {
			c = this.dataSource.getConnection();
			ps = stmt.makePreparedStatement(c);
			ps.executeUpdate();
		} catch(Exception e) {
			throw e;
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch(SQLException e) {
					
				}
			}
			
			if(c != null) {
				try {
					c.close();
				} catch(SQLException e) {
					
				}
			}
		}
	}
}
