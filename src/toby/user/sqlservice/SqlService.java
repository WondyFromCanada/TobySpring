package toby.user.sqlservice;

public interface SqlService {
	String getSql(String key) throws SqlRetrievalFailureException; //런타임 예외
}