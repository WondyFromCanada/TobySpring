package toby.user.sqlservice.updatable;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import toby.user.sqlservice.SqlNotFoundException;
import toby.user.sqlservice.SqlUpdateFailureException;
import toby.user.sqlservice.UpdatableSqlRegistry;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

//UpdatableSqlRegistry 인터페이스를 구현한 모든 클래스에 대한 테스트를 만들 때 사용할 수 있는 추상 테스트 클래스이다.
public abstract class AbstractUpdatableSqlRegistryTest {
	UpdatableSqlRegistry sqlRegistry;
	
	@Before
	public void setUp() {
		sqlRegistry = createUpdatableSqlRegistry();
		sqlRegistry.registerSql("KEY1", "SQL1");
		sqlRegistry.registerSql("KEY2", "SQL2");
		sqlRegistry.registerSql("KEY3", "SQL3");
	}
	
	//테스트 픽스처를 생성하는 부분만 추상 메소드로 만들어두고 서브클래스에서 이를 구현하도록 만든다.
	abstract protected UpdatableSqlRegistry createUpdatableSqlRegistry();

	@Test
	public void find() {
		checkFind("SQL1", "SQL2", "SQL3");
	}
	
	@Test(expected=SqlNotFoundException.class)
	public void unknownKey() {
		sqlRegistry.findSql("SQL9999!@#$");
	}
	
	protected void checkFind(String expected1, String expected2, String expected3) {
		assertThat(sqlRegistry.findSql("KEY1"), is(expected1));		
		assertThat(sqlRegistry.findSql("KEY2"), is(expected2));		
		assertThat(sqlRegistry.findSql("KEY3"), is(expected3));		
	}
	
	@Test
	public void updateSingle() {
		sqlRegistry.updateSql("KEY2", "Modified2");
		
		checkFind("SQL1", "Modified2", "SQL3");
	}
	
	@Test
	public void updateMulti() {
		Map<String, String> sqlmap = new HashMap<String, String>();
		sqlmap.put("KEY1", "Modified1");
		sqlmap.put("KEY3", "Modified3");
		
		sqlRegistry.updateSql(sqlmap);
		
		checkFind("Modified1", "SQL2", "Modified3");
	}
	
	@Test(expected=SqlUpdateFailureException.class)
	public void updateWithNotExistingKey() {
		sqlRegistry.updateSql("SQL9999!@#$", "Modified2");
	}
}
