package toby.user.sqlservice;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import toby.user.dao.UserDao;
import toby.user.sqlservice.jaxb.SqlType;
import toby.user.sqlservice.jaxb.Sqlmap;

public class JaxbXmlSqlReader implements SqlReader {
	//상수로 만들어주면 의도가 코드에 명확히 드러난다.
	private static final String DEFAULT_SQLMAP_FILE = "sqlmap.xml";

	private String sqlmapFile = DEFAULT_SQLMAP_FILE;
	
	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}

	@Override
	public void read(SqlRegistry sqlRegistry) {
		String contextPath = Sqlmap.class.getPackage().getName();

		try {
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStream is = UserDao.class.getResourceAsStream(sqlmapFile);
			Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);
			for(SqlType sql : sqlmap.getSql()) {
				sqlRegistry.registerSql(sql.getKey(), sql.getValue());
			}
		} catch(JAXBException e) {
			throw new RuntimeException(e);
		}
	}
}