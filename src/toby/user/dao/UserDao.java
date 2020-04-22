package toby.user.dao;

import java.util.List;

import toby.user.domain.User;

public interface UserDao {
	void add(User user);
	User get(String id);
	void deleteAll();
	int getCount();
	List<User> getAll();
	public void update(User user1);
}
