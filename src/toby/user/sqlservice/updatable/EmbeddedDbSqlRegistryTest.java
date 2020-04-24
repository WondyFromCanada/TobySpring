package toby.user.sqlservice.updatable;

import static org.junit.Assert.fail;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import toby.user.sqlservice.SqlUpdateFailureException;
import toby.user.sqlservice.UpdatableSqlRegistry;

public class EmbeddedDbSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {
	EmbeddedDatabase db;

	@Test
	public void transactionalUpdate() {
		//초기 상태를 확인한다.
		checkFind("SQL1", "SQL2", "SQL3");
		
		Map<String, String> sqlmap = new HashMap<String, String>();
		sqlmap.put("KEY1", "Modified1");
		//존재하지 않는 키로 지정하여 테스트를 실패하게 만든 다음 롤백이 일어나는지 확인한다.
		sqlmap.put("KEY9999!@#$", "Modified9999");
		
		try {
			sqlRegistry.updateSql(sqlmap);
			fail("문제가 뭘까"); //예외가 발생했는데 catch 블록으로 넘어가지 않을 경우 테스트를 강제로 실패하게 만든다.
		} catch(SqlUpdateFailureException e) {
		}
		
		//롤백이 되었는지 확인
		checkFind("SQL1", "SQL2", "SQL3");
	}
	
	@Override
	protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
		db = new EmbeddedDatabaseBuilder()
				.setType(HSQL)
				.addScript("classpath:toby/user/sqlservice/updatable/sqlRegistrySchema.sql")
				.build();

		EmbeddedDbSqlRegistry embeddedDbSqlRegistry = new EmbeddedDbSqlRegistry();
		embeddedDbSqlRegistry.setDataSource(db);

		return embeddedDbSqlRegistry;
	}

	@After
	public void tearDown() {
		db.shutdown();
	}
}
