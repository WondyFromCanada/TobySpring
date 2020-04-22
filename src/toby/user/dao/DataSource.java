package toby.user.dao;

import java.sql.Connection;
import java.sql.Wrapper;

import javax.sql.CommonDataSource;

public interface DataSource extends CommonDataSource, Wrapper{
	Connection getConnection() throws Exception;
}
