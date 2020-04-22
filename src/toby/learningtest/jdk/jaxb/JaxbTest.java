package toby.learningtest.jdk.jaxb;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import toby.user.sqlservice.jaxb.SqlType;
import toby.user.sqlservice.jaxb.Sqlmap;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class JaxbTest {
	@Test
	public void readSqlmap() throws JAXBException, IOException {
		String contextPath = Sqlmap.class.getPackage().getName();
		
		//바인딩용 클래스들 위치를 가지고 JAXB 컨텍스트를 만든다.
		JAXBContext context = JAXBContext.newInstance(contextPath);
		
		//언마샬러 생성
		Unmarshaller unmarshaller = context.createUnmarshaller();
		
		//언마샬을 하면 매핑된 오브젝트 트리의 루트인 Sqlmap을 돌려준다.
		//테스트 클래스와 같은 폴더에 있는 XML 파일을 사용한다.
		Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(getClass().getResourceAsStream("sqlmap.xml"));
		
		List<SqlType> sqlList = sqlmap.getSql();
		
		//리스트에 담겨 있는 Sql 오브젝트를 가져와 XML문서와 같은 정보를 가지고 있는지 확인한다.
		assertThat(sqlList.size(), is(3));
		assertThat(sqlList.get(0).getKey(), is("add"));
		assertThat(sqlList.get(0).getValue(), is("insert"));
		assertThat(sqlList.get(1).getKey(), is("get"));
		assertThat(sqlList.get(1).getValue(), is("select"));
		assertThat(sqlList.get(2).getKey(), is("delete"));
		assertThat(sqlList.get(2).getValue(), is("delete"));
		
	}
}