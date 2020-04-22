package toby.user.dao;

import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.List;

import org.junit.Before;

import static org.hamcrest.CoreMatchers.is;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import toby.user.domain.Level;
import toby.user.domain.User;

/* client */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-applicationContext.xml")
public class UserDaoTest {
	@Autowired
	private ApplicationContext context;
	//setUp() 메소드에서 만드는 오브젝트를 테스트 메소드에서 사용할 수 있도록 인스턴스 변수로 선언한다.
	@Autowired
	UserDaoJdbc dao; //UserDao 타입 빈을 직접 DI 받는다.
	
	private User user1;
	private User user2;
	private User user3;
	
	@Before //JUnit이 제공하는 어노테이션, @Test 메소드가 실행되기 전에 먼저 실행되어야 하는 메소드를 정의한다.
	public void setUp() {
		//ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		
		this.dao = context.getBean("userDao", UserDaoJdbc.class);
		
		this.user1 = new User("dongko", "김동코", "5678", "dhkim@naver.com", Level.BASIC, 1, 0);
		this.user2 = new User("sunji", "김선지", "1004", "sunji6@naver.com", Level.SILVER, 55, 10);
		this.user3 = new User("wondy", "김정언", "1234", "jeng1106@naver.com", Level.GOLD, 100, 40);
	}
	
	/*public static void main(String args[]) throws Exception {
		//ConnectionMaker connectionMaker = new DConnectionMaker();
		
		//UserDao userDao = new DaoFactory().userDao();
		
		//ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
		
		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		UserDao dao = context.getBean("userDao", UserDao.class);
		
		User user = new User();
		user.setId("wondy");
		user.setName("김정언");
		user.setPassword("1234");
		
		dao.add(user);
		
		System.out.println(user.getId() + " 등록 성공");
		
		User user2 = dao.get(user.getId());
		//System.out.println(user2.getName());
		//System.out.println(user2.getPassword());
		
		//System.out.println(user2.getId() + " 조회 성공");
		
		// 2020-03-25 UserDaoTest 개선
		if (!user.getName().equals(user2.getName())) {
			System.out.println("테스트 실패 (name)");
		} else if (!user.getPassword().equals(user2.getPassword())) {
			System.out.println("테스트 실패 (password)");
		} else {
			System.out.println("조회 테스트 성공");
		}
		// 테스트가 main() 메소드로 만들어졌다는 것은 제어권을 직접 갖는다는 의미이기 때문에 프레임워크에 적용하기에 적합하지 않다.
	}*/
	
	// 2020-03-25 JUnit 프레임워크에서 동작할 수 있는 테스트 메소드로 변환
	@Test //JUnit 에게 테스트용 메소드임을 알려준다.
	// 테스트의 의도가 무엇인지 알 수 있는 이름 권장
	public void addAndGet() throws Exception { //JUnit 테스트 메소드는 반드시 public 으로 선언되어야 한다.
		// GenericXmlApplicationContext : 항상 root에서부터 시작하는 클래스패스, /는 생략 가능
		// ClassPathXmlApplicationContext : xml과 같은 클래스패스에 있는 클래스 오브젝트를 넘겨서 클래스패스에 대한 힌트를 제공 p.135
		//ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		
		//UserDao dao = context.getBean("userDao", UserDao.class);
		//User user1 = new User("kos", "김동코우", "5858");
		//User user2 = new User("jji", "김언정찌", "7777");
		
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		User userget1 = dao.get(user1.getId());
		checkSameUser(userget1, user1);
		/*assertThat(userget1.getName(), is(user1.getName()));
		assertThat(userget1.getPassword(), is(user1.getPassword()));*/
		
		User userget2 = dao.get(user2.getId());
		checkSameUser(userget2, user2);
		/*assertThat(userget2.getName(), is(user2.getName()));
		assertThat(userget2.getPassword(), is(user2.getPassword()));*/
		
		//User user2 = dao.get(user.getId());
		
		//스태틱 메소드
		//assertThat(user2.getName(), is(user.getName()));
		//is()는 매처의 일종으로 equals()로 비교해주는 기능
		//assertThat(user2.getPassword(), is(user.getPassword()));
	}
	
	@Test
	public void count() throws Exception {
		//ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		
		//UserDao dao = context.getBean("userDao", UserDao.class);
		//User user1 = new User("wondy", "김정언", "1234");
		//User user2 = new User("dongko", "김동코", "5678");
		//User user3 = new User("sunji", "김선지", "1004");
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		assertThat(dao.getCount(), is(1));
		
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		dao.add(user3);
		assertThat(dao.getCount(), is(3));
	}
	
	@Test(expected=EmptyResultDataAccessException.class)
	public void getUserFailure() throws Exception {
		//ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		
		//UserDao dao = context.getBean("userDao", UserDao.class);
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.get("unknown_id"); //메소드 실행 중에 예외가 발생해야 한다. 예외가 발생하지 않으면 테스트가 실패한다.
	}
	
	@Test
	public void getAll() throws Exception {
		dao.deleteAll();
		
		List<User> users0 = dao.getAll();
		assertThat(users0.size(), is(0));
		
		dao.add(user1);
		List<User> users1 = dao.getAll();
		assertThat(users1.size(), is(1));
		checkSameUser(user1, users1.get(0));
		
		dao.add(user2);
		List<User> users2 = dao.getAll();
		assertThat(users2.size(), is(2));
		checkSameUser(user1, users2.get(0));
		checkSameUser(user2, users2.get(1));
		
		dao.add(user3);
		List<User> users3 = dao.getAll();
		assertThat(users3.size(), is(3));
		checkSameUser(user1, users3.get(0));
		checkSameUser(user2, users3.get(1));
		checkSameUser(user3, users3.get(2));
	}
	
	private void checkSameUser(User user1, User user2) {
		assertThat(user1.getId(), is(user2.getId()));
		assertThat(user1.getName(), is(user2.getName()));
		assertThat(user1.getPassword(), is(user2.getPassword()));
		assertThat(user1.getEmail(), is(user2.getEmail()));		
		assertThat(user1.getLevel(), is(user2.getLevel()));
		assertThat(user1.getLogin(), is(user2.getLogin()));
		assertThat(user1.getRecommend(), is(user2.getRecommend()));
	}
	
	@Test (expected=DataAccessException.class)/*(expected=DuplicateKeyException.class)*/
	public void duplicateKey() {
		dao.deleteAll();
		
		dao.add(user1);
		dao.add(user1);
	}
	
	@Autowired
	private javax.sql.DataSource dataSource;
	
	@Test
	public void sqlExceptionTranslator() {
		dao.deleteAll();
		
		try {
			dao.add(user1);
			dao.add(user1);
		} catch(DuplicateKeyException ex) {
			SQLException sqlEx = (SQLException) ex.getRootCause();
			SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
			assertThat(set.translate(null, null, sqlEx), 
					is(DuplicateKeyException.class));
		}
	}
	
	@Test
	public void update() {
		dao.deleteAll();
		
		dao.add(user1); //수정할 사용자
		dao.add(user2); //수정하지 않을 사용자
		
		user1.setName("언정킴");
		user1.setPassword("1004");
		user1.setEmail("wondy@naver.com");
		user1.setLevel(Level.GOLD);
		user1.setLogin(1000);
		user1.setRecommend(999);
		dao.update(user1);
		
		User user1update = dao.get(user1.getId());
		checkSameUser(user1, user1update);
		User user2same = dao.get(user2.getId());
		checkSameUser(user2, user2same);
	}
	
	public static void main(String[] args) {
		//어디에든 main() 메소드를 추가하고 JUnitCore 클래스의 main 메소드를 호출해주는 코드를 넣으면 된다.
		//메소드 파라미터에는 @Test 테스트 메소드를 가진 클래스 이름을 넣어준다.
		JUnitCore.main("toby.UserDaoTest");
		//테스트를 실행하는 데 걸린 시간, 테스트 결과, 몇개의 테스트 메소드가 실행되었는지 알려준다.
	}
}