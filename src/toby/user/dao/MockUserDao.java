package toby.user.dao;

import java.util.ArrayList;
import java.util.List;

import toby.user.domain.User;

public class MockUserDao implements UserDao {
	private List<User> users; //레벨 업그레이드 후보 User 오브젝트 목록
	private List<User> updated = new ArrayList<User>(); //업그레이드 대상 오브젝트를 저장해둘 목록
	
	public MockUserDao(List<User> users) {
		this.users = users;
	}
	
	public List<User> getUpdated(){
		return this.updated;
	}
	
	@Override
	public void add(User user) {
		// 테스트에 사용되지 않는 메소드
		throw new UnsupportedOperationException();
	}

	@Override
	public User get(String id) {
		// 테스트에 사용되지 않는 메소드
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAll() {
		// 테스트에 사용되지 않는 메소드
		throw new UnsupportedOperationException();
	}

	@Override
	public int getCount() {
		// 테스트에 사용되지 않는 메소드
		throw new UnsupportedOperationException();
	}

	@Override
	public List<User> getAll() {
		return this.users;
	}

	@Override
	public void update(User user) {
		updated.add(user);
	}

}
