package toby.user.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import toby.user.dao.MockUserDao;
import toby.user.dao.UserDao;
import toby.user.domain.Level;
import toby.user.domain.User;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import static toby.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static toby.user.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-applicationContext.xml")
@Transactional
//롤백 여부에 대한 기본 설정과 트랜잭션 매니저 빈을 지정하는 데 사용할 수 있다. 디폴트 트랜잭션 매니저 아이디는 관례를 따라서 transactionManager로 되어 있다.
@TransactionConfiguration(defaultRollback=false)
public class UserServiceTest {
	@Autowired
	UserService userService;
	@Autowired
	UserDao userDao;
	@Autowired
	DataSource dataSource;
	@Autowired
	PlatformTransactionManager transactionManager;
	@Autowired
	MailSender mailSender;
	/*@Autowired
	UserServiceImpl userServiceImpl;*/
	@Autowired
	ApplicationContext context; //팩토리 빈을 가져오려면 애플리케리션 컨텍스트가 필요하다.
	@Autowired
	UserService testUserService;
	
	List<User> users; //테스트 픽스처
	
	public UserServiceTest() {}
	
	@Before
	public void setUp() {
		
		users = Arrays.asList(
				new User("user1", "언정킴", "pwd1", "email1@example.com", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0),
				new User("user2", "동코킴", "pwd2", "email2@example.com", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
				new User("user3", "부농이", "pwd3", "email3@example.com", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1),
				new User("user4", "에비츄", "pwd4", "email4@example.com", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD),
				new User("user5", "순이찌", "pwd5", "email5@example.com", Level.GOLD, 100, Integer.MAX_VALUE)
				);
	}
	
	@Test
	@DirtiesContext //컨텍스트의 DI 설정을 변경하는 테스트라는 것을 알려준다.
	public void upgradeLevels() throws Exception {
		//고립된 테스트에서는 테스트 대상 오브젝트를 직접 생성하면 된다.
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		MockUserDao mockUserDao = new MockUserDao(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		
		/*userDao.deleteAll();
		
		for(User user : users) {
			userDao.add(user);
		}*/
		
		//메일 발송 결과를 테스트할 수 있도록 목 오브젝트를 만들어 userService의 의존 오브젝트를 주입해준다.
		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpl.setMailSender(mockMailSender);
		
		//업그레이드 테스트와 메일발송이 일어나면 MockMailSender 오브젝트의 리스트에 결과가 저장된다.
		userServiceImpl.upgradeLevels();
		
		List<User> updated = mockUserDao.getUpdated();
		assertThat(updated.size(), is(2));
		checkUserAndLevel(updated.get(0), "user2", Level.SILVER);
		checkUserAndLevel(updated.get(1), "user4", Level.GOLD);
		/*checkLevelUpgraded(users.get(0), false);
		checkLevelUpgraded(users.get(1), true);
		checkLevelUpgraded(users.get(2), false);
		checkLevelUpgraded(users.get(3), true);
		checkLevelUpgraded(users.get(4), false);*/
		
		//목 오브젝트에 저장된 메일 수신자 목록을 가져와 업그레이드 대상과 일치하는지 확인한다.
		List<String> request = mockMailSender.getRequests();
		assertThat(request.size(), is(2));
		assertThat(request.get(0), is(users.get(1).getEmail()));
		assertThat(request.get(1), is(users.get(3).getEmail()));
		
		//업그레이드 후 예상 레벨 검증
		/*checkLevel(users.get(0), Level.BASIC);
		checkLevel(users.get(1), Level.SILVER);
		checkLevel(users.get(2), Level.SILVER);
		checkLevel(users.get(3), Level.GOLD);
		checkLevel(users.get(4), Level.GOLD);*/
	}
	
	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
		assertThat(updated.getId(), is(expectedId));		
		assertThat(updated.getLevel(), is(expectedLevel));		
	}

	//upgraded : 다음 레벨로 업그레이드 될 것인지 지정
	private void checkLevelUpgraded(User user, boolean upgraded) {
		User userUpdate = userDao.get(user.getId());
		
		if(upgraded) {
			//업그레이드가 일어났는지 확인
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
		} else {
			//업그레이드가 일어나지 않았는지 확인
			assertThat(userUpdate.getLevel(), is(user.getLevel()));
		}
	}

	private void checkLevel(User user, Level expectedLevel) {
		User userUpdate = userDao.get(user.getId());
		assertThat(userUpdate.getLevel(), is(expectedLevel));
	}
	
	@Test
	public void add() {
		userDao.deleteAll();
		
		User userWithLevel = users.get(4);
		User userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null);
		
		userService.add(userWithLevel);
		userService.add(userWithoutLevel);
		
		//DB에 저장된 결과 가져오기
		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());
		
		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
		assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
	}
	
	@Test
	@DirtiesContext //컨텍스트 무효화 애노테이션
	public void upgradeAllOrNothing() throws Exception {
		//테스트를 발생시킬 때 4번째 사용자의 id를 넣어서 TestUserService 오브젝트를 생성한다.
		//TestUserServiceImpl testUserService = new TestUserServiceImpl(users.get(3).getId());
		//UserDao를 수동으로 DI 해준다.
		//-> TestUserService는 이 메소드에서만 특별한 목적으로 사용되는 것이니, 번거롭게 스프링 빈으로 등록하지 않는다.
		//testUserService.setUserDao(userDao);
		//testUserService.setTransactionManager(transactionManager);
		//testUserService.setMailSender(mailSender);
		/*testUserService.setDataSource(this.dataSource);*/
		
		/*UserServiceTx txUserService = new UserServiceTx();
		txUserService.setTransactionManager(transactionManager);
		txUserService.setUserService(testUserService);*/
		
		/*TransactionHandler txHandler = new TransactionHandler();
		txHandler.setTarget(testUserService);
		txHandler.setTransactionManager(transactionManager);
		txHandler.setPattern("upgradeLevels");
		
		UserService txUserService = (UserService) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {UserService.class}, txHandler);*/
		
		//TxProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", TxProxyFactoryBean.class);
		//ProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", ProxyFactoryBean.class);
		//txProxyFactoryBean.setTarget(testUserService);
		
		//UserService txUserService = (UserService) txProxyFactoryBean.getObject();
			
		userDao.deleteAll();
		for(User user : users) userDao.add(user);
		
		try {
			this.testUserService.upgradeLevels();
			//txUserService.upgradeLevels();
			//testUserService.upgradeLevels();
			fail("TestUserServiceException expected");
		} catch(TestUserServiceException e) {
			//TestUserService가 던져주는 예외가 아니라면 테스트 실패
		}
		
		//예외가 발생하기 전에 레벨 변경이 있었던 사용자의 레벨이 처음 상태로 바뀌었나 확인
		checkLevelUpgraded(users.get(1), false);
	}
	
	/*@Test
	public void bean() {
		assertThat(this.userService, is(notNullValue()));
	}*/
	
	@Test
	public void mockUpgradeLevels() {
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		UserDao mockUserDao = mock(UserDao.class);
		when(mockUserDao.getAll()).thenReturn(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		
		MailSender mockMailSender = mock(MailSender.class);
		userServiceImpl.setMailSender(mockMailSender);
		
		userServiceImpl.upgradeLevels();
		
		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao).update(users.get(1));
		assertThat(users.get(1).getLevel(), is(Level.SILVER));
		verify(mockUserDao).update(users.get(3));
		assertThat(users.get(3).getLevel(), is(Level.GOLD));
		
		ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mockMailSender, times(2)).send(mailMessageArg.capture());
		List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
		assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
		assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));
	}
	
	static class MockMailSender implements MailSender {
		//UserService로 부터 전송 요청을 받은 메일 주소를 저장해두고 읽을 수 있도록 한다.
		private List<String> requests = new ArrayList<String>();
		
		public List<String> getRequests() {
			return requests;
		}
		
		@Override
		public void send(SimpleMailMessage mailMessage) throws MailException {
			//전송 요청을 받은 이메일 주소를 저장해둔다.
			requests.add(mailMessage.getTo()[0]); //첫 번째 수신자 메일 주소만 지정
		}
		
		@Override
		public void send(SimpleMailMessage[] arg0) throws MailException {
			
		}
	}
	
	static class TestUserServiceImpl extends UserServiceImpl {
		private String id = "user4";

		public TestUserServiceImpl() {}
		
		//예외를 발생시킬 User 오브젝트의 id를 지정할 수 있게 만든다.
		protected TestUserServiceImpl(String id) {
			this.id = id;
		}
		
		protected void upgradeLevel(User user) { //UserService의 메소드를 오버라이드 한다.
			if(user.getId().equals(this.id)) throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
		
		public List<User> getAll() {
			for(User user : super.getAll()) {
				super.update(user);
			}
			return null;
		}
	}
	
	static class TestUserServiceException extends RuntimeException {
	}
	
	@Test
	public void advisorAutoProxyCreator() {
		assertThat(testUserService, is(java.lang.reflect.Proxy.class));
	}
	
	@Test(expected=TransientDataAccessResourceException.class)
	public void readOnlyTransactionAttribute() {
		//트랜잭션 속성이 제대로 적용되었다면 여기서 읽기전용 속성을 위반했기 때문에 예외가 발생해야 한다.
		testUserService.getAll();
	}
	
	@Test
	@Transactional(propagation=Propagation.NEVER)/*(readOnly=true)*/
	/*@Rollback(false)*/
	public void transactionSync() {
		userService.deleteAll();
		userService.add(users.get(0));
		userService.add(users.get(1));
		
		//assertThat(userDao.getCount(), is(0));
		
		//DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
		
		//읽기전용 트랜잭션으로 정의한다.
		//txDefinition.setReadOnly(true);
		
		//TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);
		
		
		//assertThat(userDao.getCount(), is(2));
		
		//transactionManager.commit(txStatus);
		
		//assertThat(userDao.getCount(), is(0));
		
		/*try {
			userService.deleteAll();
			userService.add(users.get(0));
			userService.add(users.get(1));
		} finally {
			transactionManager.rollback(txStatus);
		}*/
	}
}