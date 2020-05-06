package toby;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mysql.jdbc.Driver;

import toby.user.dao.UserDao;
import toby.user.service.DummyMailSender;
import toby.user.service.UserService;
import toby.user.service.UserServiceImpl;
import toby.user.service.UserServiceTest.TestUserServiceImpl;
import toby.user.sqlservice.OxmSqlService;
import toby.user.sqlservice.SqlRegistry;
import toby.user.sqlservice.SqlService;
import toby.user.sqlservice.updatable.EmbeddedDbSqlRegistry;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages="toby.user")
//@Import(SqlServiceContext.class)
@EnableSqlService
@PropertySource("/database.properties")
public class AppContext implements SqlMapConfig{

	/**
	 * DB연결과 트랜잭션
	 */
	
	@Value("${db.driverClass}") Class<? extends Driver> driverClass;
	@Value("${db.url}") String url;
	@Value("${db.username}") String username;
	@Value("${db.password}") String password;

	@Autowired Environment env;
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	public DataSource dataSource() {
		/*SimpleDriverDataSource ds = new SimpleDriverDataSource();
		ds.setDriverClass(Driver.class);
		ds.setUrl("jdbc:mysql://localhost/testdb?characterEncoding=UTF-8");
		ds.setUsername("root");
		ds.setPassword("1234");
		return ds;*/
		
		SimpleDriverDataSource ds = new SimpleDriverDataSource();

		ds.setDriverClass(this.driverClass);
		ds.setUrl(this.url);
		ds.setUsername(this.username);
		ds.setPassword(this.password);
		
		return ds;
	}

	/*@Bean
	public SqlMapConfig sqlMapConfig() {
		return new UserSqlMapConfig();
	}*/

	@Override
	public Resource getSqlMapResouce() {
		return new ClassPathResource("sqlmap.xml", UserDao.class);
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager tm = new DataSourceTransactionManager();
		tm.setDataSource(dataSource());
		return tm;
	}

	@Configuration
	@Profile("production")
	public static class ProductionAppContext {
		@Bean
		public MailSender mailSender() {
			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			mailSender.setHost("localhost");
			return mailSender;
		}
	}

	@Configuration
	@Profile("test")
	public static class TestAppContext {
		/**
		 * 애플리케이션 로직 & 테스트용 빈
		 */

		@Bean
		public UserService testUserService() {
			return new TestUserServiceImpl();
		}

		@Bean
		public MailSender mailSender() {
			return new DummyMailSender();
		}
	}
}