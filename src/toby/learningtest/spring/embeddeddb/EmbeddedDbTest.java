package toby.learningtest.spring.embeddeddb;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
public class EmbeddedDbTest {
	EmbeddedDatabase db;
	SimpleJdbcTemplate template; //JdbcTemplate을 더 편리하게 사용할 수 있게 확장한 템플릿
	
	@Before
	public void setUp() {
		db = new EmbeddedDatabaseBuilder()
				.setType(HSQL)
				.addScript("classpath:/toby/learningTest/spring/embeddeddb/schema.sql")
				.addScript("classpath:/toby/learningTest/spring/embeddeddb/data.sql")
				.build();
		
		//EmbeddedDatabase는 DataSource의 서브 인터페이스이므로 DataSource를 필요로 하는 SimpleJdbcTemplate을 만들 떄 사용할 수 있다.
		template = new SimpleJdbcTemplate(db);
	}
	
	//매 테스트를 진행한 뒤 DB를 종료한다.
	@After
	public void tearDown() {
		db.shutdown();
	}
	
	//초기화 스크립트를 통해 등록된 데이터를 검증하는 테스트이다.
	@Test
	public void initData() {
		assertThat(template.queryForInt("SELECT COUNT(*) FROM SQLMAP"), is(2));
		
		List<Map<String, Object>> list = template.queryForList("SELECT * FROM SQLMAP ORDER BY KEY_");
		assertThat(list.get(0).get("KEY_"), is("KEY1"));
		assertThat(list.get(0).get("SQL_"), is("SQL1"));
		assertThat(list.get(1).get("KEY_"), is("KEY2"));
		assertThat(list.get(1).get("SQL_"), is("SQL2"));
	}
	
	//새로운 데이터를 추가하고 확인해보는 테스트이다.
	@Test
	public void insert() {
		template.update("INSERT INTO SQLMAP(KEY_, SQL_) VALUES(?,?)", "KEY3", "SQL3");
		
		assertThat(template.queryForInt("SELECT COUNT(*) FROM SQLMAP"), is(3));
	}
}
