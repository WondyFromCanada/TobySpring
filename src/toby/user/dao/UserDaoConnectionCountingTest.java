package toby.user.dao;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import toby.user.domain.User;

public class UserDaoConnectionCountingTest {
	public static void main(String args[]) throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CountingDaoFactory.class);
		
		UserDao dao = context.getBean("userDao", UserDao.class);
		User user = dao.get("wondy");
		
		CountingConnectionMaker ccm = context.getBean("connectionMaker", CountingConnectionMaker.class);
		
		System.out.println("userId : " + user.getId());
		System.out.println("ID : " + dao.get("wondy"));
		System.out.println("Connection Counter : " + ccm.getCounter());
	}
}
