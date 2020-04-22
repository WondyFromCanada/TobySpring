package toby.user.sqlservice;

public class SqlNotFoundException extends Exception {
	public SqlNotFoundException() {
		super();
	}

	public SqlNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public SqlNotFoundException(String message) {
		super(message);
	}

	public SqlNotFoundException(Throwable cause) {
		super(cause);
	}
}