package toby.user.sqlservice.updatable;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import toby.user.sqlservice.SqlNotFoundException;
import toby.user.sqlservice.SqlUpdateFailureException;
import toby.user.sqlservice.UpdatableSqlRegistry;

public class EmbeddedDbSqlRegistry implements UpdatableSqlRegistry {
	SimpleJdbcTemplate jdbc;
	TransactionTemplate transactionTemplate;
	
	public void setDataSource(DataSource dataSource) {
		//DataSource를 DI 받아서 SimpleJdbcTemplate 형태로 지정해두고 사용한다.
		jdbc = new SimpleJdbcTemplate(dataSource);
		/*
		 * jdbc 스키마의 태그를 이용해 내장형 DB로 정의한 빈의 타입은 EmbeddedDatabase인데 왜 DataSource 타입으로 DI 받을까?
		 * 인터페이스 분리 원칙을 지키기 위해서이다.
		 * 클라이언트는 자신이 필요로 하는 기능을 가진 인터페이스를 통해 의존 오브젝트를 DI해야 한다.
		 * SqlRegistry는 JDBC를 이용해 DB에 접근만 할 수 있으면 되기 때문에 사용하지 않을 DB 종료 기능을 가진 EmbeddedDatabase 보다 
		 * DataSource가 적합하다. 따라서 DataSource의 서브 인터페이스인 EmbeddedDatabase 대신 DataSource 인터페이스를 사용한 것이다.
		 * */
		//dataSource로 TransactionManager를 만들고 이를 이용해 TransactionTemplate을 생성한다.
		transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
		transactionTemplate.setIsolationLevel(TransactionTemplate.ISOLATION_READ_COMMITTED);
	}
	
	@Override
	public void registerSql(String key, String sql) {
		jdbc.update("INSERT INTO SQLMAP(KEY_, SQL_) VALUES(?,?)", key, sql);
	}
	
	@Override
	public String findSql(String key) throws SqlNotFoundException {
		try {
			return jdbc.queryForObject("SELECT SQL_ FROM SQLMAP WHERE KEY_ = ?", String.class, key);
		}
		catch(EmptyResultDataAccessException e) {
			throw new SqlNotFoundException(key + "에 해당하는 SQL을 찾을 수 없습니다", e);
		}
	}
	
	@Override
	public void updateSql(String key, String sql) throws SqlUpdateFailureException {
		int affected = jdbc.update("UPDATE SQLMAP SET SQL_ = ? WHERE KEY_ = ?" , sql, key);
		if (affected == 0) {
			throw new SqlUpdateFailureException(key + "에 해당하는 SQL을 찾을 수 없습니다");
		}
	}
	
	@Override
	//익명 내부 클래스로 만들어지는 콜백 오브젝트 안에서 사용되는 것이라 final로 선언해주어야 한다.
	public void updateSql(final Map<String, String> sqlmap) throws SqlUpdateFailureException {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			//트랜잭션 템플릿이 만드는 트랜잭션 경계 안에서 동작할 코드를 콜백 형태로 만들고 TransactionTemplate의 execute() 메소드에 전달한다.
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				for(Map.Entry<String, String> entry : sqlmap.entrySet()) {
					updateSql(entry.getKey(), entry.getValue());
				}			
			}
		});
	}

}