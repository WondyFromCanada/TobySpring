package toby;

import javax.sql.DataSource;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import toby.user.dao.UserDao;
import toby.user.sqlservice.OxmSqlService;
import toby.user.sqlservice.SqlRegistry;
import toby.user.sqlservice.SqlService;
import toby.user.sqlservice.updatable.EmbeddedDbSqlRegistry;

/**
 * SQL서비스
 */

@Configuration
public class SqlServiceContext {
	@Autowired SqlMapConfig sqlMapConfig;
	
	@Bean
	public SqlService sqlService() {
		OxmSqlService sqlService = new OxmSqlService();
		sqlService.setUnmarshaller(unmarshaller());
		sqlService.setSqlRegistry(sqlRegistry());
		sqlService.setSqlmap(this.sqlMapConfig.getSqlMapResouce());
		return sqlService;
	}

	@Bean
	public SqlRegistry sqlRegistry() {
		EmbeddedDbSqlRegistry sqlRegistry = new EmbeddedDbSqlRegistry();
		sqlRegistry.setDataSource(embeddedDatabase());
		return sqlRegistry;
	}

	@Bean
	public Unmarshaller unmarshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("toby.user.sqlservice.jaxb");
		return marshaller;
	}

	@Bean 
	public DataSource embeddedDatabase() {
		return new EmbeddedDatabaseBuilder()
				.setName("embeddedDatabase")
				.setType(HSQL)
				.addScript("classpath:toby/user/sqlservice/updatable/sqlRegistrySchema.sql")
				.build();
	}
}
