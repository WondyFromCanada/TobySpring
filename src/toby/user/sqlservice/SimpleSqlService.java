package toby.user.sqlservice;

import java.util.Map;

public class SimpleSqlService implements SqlService {
	private Map<String, String> sqlMap;
	
	//설정 파일에 <map>으로 정의된 SQL 정보를 가져오도록 프로퍼티로 등록해둔다.
	public void setSqlMap(Map<String, String> sqlMap) {
		this.sqlMap = sqlMap;
	}

	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		String sql = sqlMap.get(key); //내부 SqlMap에서 SQL을 가져온다.
		if(sql == null) {
			throw new SqlRetrievalFailureException(key + "를 이용해서 SQL을 찾을 수 없습니다.");
		} else {
			return sql;
		}
	}
}