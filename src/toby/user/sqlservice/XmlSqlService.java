package toby.user.sqlservice;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import toby.user.dao.UserDao;
import toby.user.sqlservice.jaxb.SqlType;
import toby.user.sqlservice.jaxb.Sqlmap;

public class XmlSqlService implements SqlService{
	//읽어올 SQL을 저장해둘 맵
	private Map<String, String> sqlMap = new HashMap<String, String>();
	//파일 이름을 외부에서 지정할 수 있도록 프로퍼티 추가
	private String sqlmapFile;
	
	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}

	//생성자 대신 사용할 초기화 메소드
	@PostConstruct //loadSql() 메소드를 빈의 초기화 메소드로 지정
	public void loadSql() {
		//JAXB API를 이용해 XML 문서를 오브젝트 트리로 읽어온다.
		String contextPath = Sqlmap.class.getPackage().getName();

		try {
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();

			//UserDao와 같은 클래스패스에 있는 sqlmap.xml 파일을 변환한다.
			//프로퍼티 설정을 통해 제공받은 파일 이름을 사용한다.
			InputStream is = UserDao.class.getResourceAsStream(this.sqlmapFile);
			Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);

			//읽어온 SQL을 맵으로 저장해둔다.
			for(SqlType sql : sqlmap.getSql()) {
				sqlMap.put(sql.getKey(), sql.getValue());
			}
		} catch(JAXBException e) {
			//JAXBException은 복구 불가능한 예외이므로 런타임 예외로 포장해서 던진다.
			throw new RuntimeException(e);
		}
	}
	
	//스프링이 오브젝트를 만드는 시점에서 SQL을 읽어오도록 생성자를 이용한다.
	public XmlSqlService() {}
	
	
	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		String sql = sqlMap.get(key);
		
		if(sql == null) {
			throw new SqlRetrievalFailureException(key + "를 이용해서 SQL을 찾을 수 없습니다.");
		} else {
			return sql;			
		}
	}
}