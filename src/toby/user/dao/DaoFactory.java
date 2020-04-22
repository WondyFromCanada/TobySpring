package toby.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//application context로 본다는 어노테이션.
@Configuration
public class DaoFactory {
	/*@Bean
	public UserDao userDao() throws Exception {
		UserDao userDao = new UserDao();
		//userDao.setConnectionMaker(makeConnection());
		
		return userDao;
	}*/
	
	@Bean
	public ConnectionMaker makeConnection() throws Exception {
		return new DConnectionMaker();
	}
}