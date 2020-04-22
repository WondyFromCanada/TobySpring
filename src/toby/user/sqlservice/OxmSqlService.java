package toby.user.sqlservice;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;

import toby.user.dao.UserDao;
import toby.user.sqlservice.jaxb.SqlType;
import toby.user.sqlservice.jaxb.Sqlmap;

public class OxmSqlService implements SqlService {
	//final이므로 변경 불가능하다.
	//OxmSqlService와 OxmSqlReader는 강하게 결합돼서 하나의 빈으로 등록되고 한 번에 설정할 수 있다.
	private final OxmSqlReader oxmSqlReader = new OxmSqlReader();
	
	//SqlService의 실제 구현 부분을 위임할 대상인 BaseSqlService를 인스턴스 변수로 정의해둔다.
	private final BaseSqlService baseSqlService = new BaseSqlService();
	
	//디폴트 오브젝트로 만들어진 프로퍼티 (DI를 통해 교체 가능하다.)
	private SqlRegistry sqlRegistry = new HashMapSqlRegistry();
	
	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}

	//OxmSqlService의 공개된 프로퍼티를 통해 DI 받은 것을 그대로 멤버 클래스의 오브젝트에 전달한다.
	//이 setter들은 단일 빈 설정구조를 위한 창구 역할을 한다.
	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.oxmSqlReader.setUnmarshaller(unmarshaller);
	}

	//이름과 타입을 모두 변경한다.
	public void setSqlmap(Resource sqlmap) {
		this.oxmSqlReader.setSqlmap(sqlmap);
	}
	
	/*public void setSqlmapFile(String sqlmapFile) {
		//OxmSqlReader로 전달만 한다.
		this.oxmSqlReader.setSqlmapFile(sqlmapFile);
	}*/
	
	@PostConstruct
	public void loadSql() {
		//OxmSqlService의 프로퍼티를 통해서 초기화된 SqlReader와 SqlRegistry를 실제 작업을 위임할 대상인 baseSqlService에 주입한다.
		this.baseSqlService.setSqlReader(this.oxmSqlReader);
		this.baseSqlService.setSqlRegistry(this.sqlRegistry);
		
		//SQL을 등록하는 초기화 작업을 baseSqlService에 위임한다.
		this.baseSqlService.loadSql();
		//this.oxmSqlReader.read(this.sqlRegistry);
	}
	
	//private 멤버 클래스로 정의한다. 톱레벨 클래스인 OxmSqlService만 사용할 수 있다.
	private class OxmSqlReader implements SqlReader {		
		private Unmarshaller unmarshaller;
		
		private Resource sqlmap = new ClassPathResource("sqlmap.xml", UserDao.class);
		
		/*private static final String DEFAULT_SQLMAP_FILE = "sqlmap.xml";
		private String sqlmapFile = DEFAULT_SQLMAP_FILE;*/
		
		public void setUnmarshaller(Unmarshaller unmarshaller) {
			this.unmarshaller = unmarshaller;
		}
		
		/*public void setSqlmapFile(String sqlmapFile) {
			this.sqlmapFile = sqlmapFile;
		}*/
		
		public void setSqlmap(Resource sqlmap) {
			this.sqlmap = sqlmap;
		}
		
		@Override
		public void read(SqlRegistry sqlRegistry) {
			try {
				//리소스의 종류에 상관없이 스트림으로 가져올 수 있다.
				Source source = new StreamSource(sqlmap.getInputStream());
				//Source source = new StreamSource(UserDao.class.getResourceAsStream(this.sqlmapFile));
				Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(source);
				
				for(SqlType sql : sqlmap.getSql()) {
					sqlRegistry.registerSql(sql.getKey(), sql.getValue());
				}
			} catch(IOException e) {
				//언마샬 작업 중 IO 에러가 났다면 설정을 통해 제공받은 XML 파일 이름이나 정보가 잘못됐을 가능성이 제일 높다.
				//이런 경우에 가장 적합한 런타임 예외 중 하나인 IllegalArgumentException로 포장해서 던진다.
				//throw new IllegalArgumentException(this.sqlmapFile + "을 가져올 수 없습니다.", e);
				throw new IllegalArgumentException(this.sqlmap.getFilename() + "을 가져올 수 없습니다.", e);
			}
		}
	}
	
	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		//SQL을 찾아오는 작업도 baseSqlService에 위임한다.
		return this.baseSqlService.getSql(key);
		/*try {
			return this.sqlRegistry.findSql(key);
		} catch(SqlNotFoundException e) {
			throw new SqlRetrievalFailureException(e);
		}*/
	}

}
