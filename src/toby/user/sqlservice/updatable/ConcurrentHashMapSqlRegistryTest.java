package toby.user.sqlservice.updatable;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import toby.user.sqlservice.SqlNotFoundException;
import toby.user.sqlservice.SqlUpdateFailureException;
import toby.user.sqlservice.UpdatableSqlRegistry;

public class ConcurrentHashMapSqlRegistryTest extends AbstractUpdatableSqlRegistryTest{

	@Override
	protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
		return new ConcurrentHashMapSqlRegistry();
	}
	/*UpdatableSqlRegistry sqlRegistry;
	
	@Before
	public void setUp() {
		sqlRegistry = new ConcurrentHashMapSqlRegistry();
		//각 테스트 메소드에서 사용할 초기 SQL 정보를 미리 등록해둔다.
		sqlRegistry.registerSql("KEY1", "SQL1");
		sqlRegistry.registerSql("KEY2", "SQL2");
		sqlRegistry.registerSql("KEY3", "SQL3");
	}
	
	@Test
	public void find() throws Exception {
		checkFindResult("SQL1", "SQL2", "SQL3");
	}

	//반복적으로 검증하는 부분은 별도의 메소드로 분리해준다.
	private void checkFindResult(String expected1, String expected2, String expected3) throws Exception {
		assertThat(sqlRegistry.findSql("KEY1"), is(expected1));
		assertThat(sqlRegistry.findSql("KEY2"), is(expected2));
		assertThat(sqlRegistry.findSql("KEY3"), is(expected3));
	}
	
	//주어진 키에 해당하는 SQL을 찾을 수 없을 때 예외가 발생하는지 확인한다.
	@Test(expected= SqlNotFoundException.class)
	public void unknownkey() throws SqlNotFoundException {
		sqlRegistry.findSql("SQL9999!@#$");
	}
	
	//하나의 SQL을 변경하는 기능에 대한 테스트이다.
	@Test
	public void updateSingle() throws Exception {
		sqlRegistry.updateSql("KEY2", "Modified2");
		//변경된 SQL 외 나머지 SQL은 그대로인지 확인한다.
		checkFindResult("SQL1", "Modified2", "SQL3");
	}
	
	//한 번에 여러 개의 SQL을 수정하는 테스트이다.
	@Test
	public void updateMulti() throws Exception {
		Map<String, String> sqlmap = new HashMap<String, String>();
		sqlmap.put("KEY1", "Modified1");
		sqlmap.put("KEY3", "Modified3");
		
		sqlRegistry.updateSql(sqlmap);
		checkFindResult("Modified1", "SQL2", "Modified3");
	}
	
	//존재하지 않는 키의 SQL을 변경하려고 할 때 예외가 발생하는 것을 검증한다.
	@Test(expected=SqlUpdateFailureException.class)
	public void updateWithNotExistingKey() throws Exception {
		sqlRegistry.updateSql("SQL9999!@#$", "Modified2");
	}*/
}
