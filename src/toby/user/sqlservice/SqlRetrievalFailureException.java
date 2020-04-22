package toby.user.sqlservice;

public class SqlRetrievalFailureException extends RuntimeException {
	public SqlRetrievalFailureException(String message) {
		super(message);
	}

	public SqlRetrievalFailureException(String message, Throwable cause) {
		//cause : SQL을 가져오는 데 실패한 근본 원인을 담을 수 있도록 중첩 예외를 저장할 수 있는 생성자
		super(message, cause);
	}
}