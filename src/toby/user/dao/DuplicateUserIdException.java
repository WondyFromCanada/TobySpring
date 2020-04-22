package toby.user.dao;

import java.sql.SQLException;

public class DuplicateUserIdException extends Exception {
	public DuplicateUserIdException(SQLException e) {
		super(e);
	}
}
