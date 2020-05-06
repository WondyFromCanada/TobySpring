package toby.user.dao;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.sun.crypto.provider.RSACipher;
import toby.user.domain.Level;
import toby.user.domain.User;
import toby.user.sqlservice.SqlService;

/* context */
@Repository
public class UserDaoJdbc implements UserDao {
	//private ConnectionMaker connectionMaker;
	//private SimpleDriverDataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	//private String sqlAdd;
	//private Map<String, String> sqlMap;
	@Autowired
	private SqlService sqlService;
	
	public UserDaoJdbc() {}
	
	/*public void setSqlService(SqlService sqlService) {
		this.sqlService = sqlService;
	}*/
	
	/*public void setSqlAdd(String sqlAdd) {
		this.sqlAdd = sqlAdd;
	}*/

	/*public void setSqlMap(Map<String, String> sqlMap) {
		this.sqlMap = sqlMap;
	}*/

	private RowMapper<User> userMapper = new RowMapper<User>() {
		
		@Override
		public User mapRow(ResultSet rs, int rownum) throws SQLException {
			User user = new User();
			user.setId(rs.getString("ID"));
			user.setName(rs.getString("NAME"));
			user.setPassword(rs.getString("PASSWORD"));
			user.setEmail(rs.getString("EMAIL"));
			user.setLevel(Level.valueOf(rs.getInt("LEVEL")));
			user.setLogin(rs.getInt("LOGIN"));
			user.setRecommend(rs.getInt("RECOMMEND"));
			
			return user;
		}
		
	};
	/*private JdbcContext jdbcContext;*/
	
	/*public void setDataSource(SimpleDriverDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;
	}*/

	@Autowired
	public void setDataSource(DataSource dataSource) {
                this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

	/*public void setJdbcContext(JdbcContext jdbcContext) {
		this.jdbcContext = jdbcContext;
	}*/
	/*public UserDao(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}*/
	
	/*public void setConnectionMaker(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}*/
	/*public void setDataSource(SimpleDriverDataSource dataSource) {
		this.dataSource = dataSource;
	}*/
	
	public void add(User user) {
		//프로퍼티로 제공받은 맵으로부터 키를 이용해서 필요한 SQL을 가져온다.
		this.jdbcTemplate.update(this.sqlService.getSql("userAdd"),
				user.getId(), user.getName(), user.getPassword(), user.getEmail(), user.getLevel().intValue(), user.getLogin(), user.getRecommend());
		
		/*this.jdbcTemplate.update("INSERT INTO USERS(ID, NAME, PASSWORD, EMAIL, LEVEL, LOGIN, RECOMMEND) VALUES (?,?,?,?,?,?,?)",
				user.getId(), user.getName(), user.getPassword(), user.getEmail(), user.getLevel().intValue(), user.getLogin(), user.getRecommend());*/
		
		/*class AddStatement implements StatementStrategy {

			@Override
			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
				PreparedStatement ps = c.prepareStatement("INSERT INTO USERS(ID, NAME, PASSWORD) VALUES(?,?,?)");
				
				ps.setString(1, user.getId());
				ps.setString(2, user.getName());
				ps.setString(3, user.getPassword());

				return ps;
			}
			
		}
		
		StatementStrategy strategy = new AddStatement();
		jdbcContextWithStatementStrategy(strategy);*/
		
		//Connection c = connectionMaker.makeConnection();
		/*Connection c = dataSource.getConnection();
		
		PreparedStatement ps = c.prepareStatement("INSERT INTO USERS(ID, NAME, PASSWORD) VALUES(?,?,?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());

		ps.executeUpdate();

		ps.close();
		c.close();*/
	}
	
	public User get(String id) {
		return this.jdbcTemplate.queryForObject(this.sqlService.getSql("userGet"), 
				new Object[] {id},
				userMapper
				);
		
		//Connection c = connectionMaker.makeConnection();
		/*Connection c = dataSource.getConnection();
		
		PreparedStatement ps = c.prepareStatement("SELECT * FROM USERS WHERE ID = ?");

		ps.setString(1, id);

		ResultSet rs = ps.executeQuery();
		
		User user = null; //User를 null 상태로 초기화
		
		if(rs.next()) {
			user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			
		}
		
		rs.close();
		ps.close();
		c.close();
		
		if(user == null) throw new EmptyResultDataAccessException(1);
		
		return user;*/
	}
	
	public void deleteAll() {
		this.jdbcTemplate.update(this.sqlService.getSql("userDeleteAll"));
		
		/*this.jdbcTemplate.update(
			new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection c) throws SQLException {
					return c.prepareStatement("DELETE FROM USERS");
				}
				
			}
		);*/
		
		/*this.jdbcContext.executeSql("DELETE FROM USERS");*/
		
		/*jdbcContextWithStatementStrategy(
			new StatementStrategy() {

				@Override
				public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
					return c.prepareStatement("DELETE FROM USERS");
				}
				
			}
		);*/
		
		/*StatementStrategy strategy = new DeleteAllStatement();
		jdbcContextWithStatementStrategy(strategy);*/
		
		/*Connection c = null;
		PreparedStatement ps = null;
		
		try {
			c = dataSource.getConnection();
			ps = c.prepareStatement("DELETE FROM USERS");
			ps.executeUpdate();			
		} catch(SQLException e) {
			throw new SQLException();
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					
				}
			}
			
			if(c != null) {
				try {
					c.close();
				} catch (SQLException e) {
					
				}
			}
		}*/
		
		/*Connection c = dataSource.getConnection();
		
		PreparedStatement ps = c.prepareStatement("DELETE FROM USERS");
		
		ps.execute();
		
		ps.close();
		c.close();*/
	}
	
	public int getCount() {
		return this.jdbcTemplate.queryForInt(this.sqlService.getSql("userGetCount"));
		
		/*return this.jdbcTemplate.query(
			new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection c) throws SQLException {
					return c.prepareStatement("SELECT COUNT(*) FROM USERS");
				}
				
			}, new ResultSetExtractor<Integer>() {

				@Override
				public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
					rs.next();
					return rs.getInt(1);
				}
				
			}
		);*/
		
		/*Connection c = dataSource.getConnection();
		
		PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM USERS");
		
		ResultSet rs = ps.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		
		rs.close();
		ps.close();
		c.close();
		
		return count;*/
	}
	
	public List<User> getAll() {
		return this.jdbcTemplate.query(this.sqlService.getSql("userGetAll"), userMapper);
	}
	
	public void update(User user) {
		this.jdbcTemplate.update(this.sqlService.getSql("userUpdate"),
				user.getName(), user.getPassword(), user.getEmail(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getId());
	}
	
	/*public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		
		try {
			c = dataSource.getConnection();
			ps = stmt.makePreparedStatement(c);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					
				}
			}
			if(c != null) {
				try {
					c.close();
				} catch (SQLException e) {
					
				}
			}
		}
	}*/
}