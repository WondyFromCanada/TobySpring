package toby.user.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import toby.user.dao.UserDao;
import toby.user.domain.Level;
import toby.user.domain.User;

public class UserServiceImpl implements UserService{
	UserDao userDao;
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;
	private DataSource dataSource;
	private PlatformTransactionManager transactionManager;
	private MailSender mailSender;
	
	public UserServiceImpl() {}
	
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	//프로퍼티 이름은 관례를 따라 transactionManager 라고 만든다.
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	//Connection을 생성할 때 사용할 DataSource를 DI 받는다.
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void upgradeLevels() {
		List<User> users = userDao.getAll();
		for(User user : users) {
			if(canUpgradeLevel(user)) {
				upgradeLevel(user);
			}
		}
		/*//트랜잭션 동기화 관리자를 이용해 동기화 작업을 초기화한다.
		TransactionSynchronizationManager.initSynchronization();
		//DB 커넥션 생성과 동기화를 함께 해주는 유틸리티 메소드
		Connection c = DataSourceUtils.getConnection(dataSource);
		c.setAutoCommit(false);*/
		
		
		/*PlatformTransactionManager transactionManager = 
				new DataSourceTransactionManager(dataSource); //JDBC 트랜잭션 추상 오브젝트 생성
		*/		
		//트랜잭션 시작
		//TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		/*try {
			upgradeLevelsInternal();
			List<User> users = userDao.getAll();
			for(User user : users) {
				if(canUpgradeLevel(user)) {
					upgradeLevel(user);
				}
				//레벨의 변화가 있는지 확인하는 플래그
				Boolean changed = null;
			
			if(user.getLevel() == Level.BASIC && user.getLogin() >= 50) {
				user.setLevel(Level.SILVER);
				changed = true;
			} else if(user.getLevel() == Level.SILVER && user.getRecommend() >= 30) {
				user.setLevel(Level.GOLD);
				changed = true;
			} else if(user.getLevel() == Level.GOLD) {
				changed = false;
			} else {
				changed = false;
			}
			
			if(changed) {
				userDao.update(user);
			}
			}
			//정상적으로 작업을 마치면 커밋
			c.commit();
			//트랜잭션 커밋
			this.transactionManager.commit(status);
		} catch(Exception e) {
			c.rollback();
			//트랜잭션 롤백
			this.transactionManager.rollback(status);
			throw e;
		} finally {
			//스프링 유틸리티 메소드를 이용해 DB 커넥션을 안전하게 닫는다.
			DataSourceUtils.releaseConnection(c, dataSource);
			//동기화 작업 종료 및 정리
			TransactionSynchronizationManager.unbindResource(this.dataSource);
			TransactionSynchronizationManager.clearSynchronization();
		}*/
	}

	private void upgradeLevelsInternal() {
		List<User> users = userDao.getAll();
		for(User user : users) {
			if(canUpgradeLevel(user)) {
				upgradeLevel(user);
			}
		}
	}

	private boolean canUpgradeLevel(User user) {
		Level currentLevel = user.getLevel();
		
		switch(currentLevel) {
			case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
			case SILVER: return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
			case GOLD: return false;
			//현재 로직에서 다룰 수 없는 레벨은 예외를 발생시킨다.
			default: throw new IllegalArgumentException("Unknown Level : " + currentLevel);
		}
	}

	protected void upgradeLevel(User user) {
		user.upgradeLevel();
		userDao.update(user);
		sendUpgradeEMail(user);
		/*if(user.getLevel() == Level.BASIC) {
			user.setLevel(Level.SILVER);
		} else if(user.getLevel() == Level.SILVER) {
			user.setLevel(Level.GOLD);
		}
		
		userDao.update(user);*/
	}

	private void sendUpgradeEMail(User user) {
		/*//MailSender 구현 클래스의 오브젝트를 생성한다.
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("mail.server.com");*/
		
		//MailMessage 인터페이스의 구현 클래스 오브젝트를 만들어 메일 내용을 작성한다.
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmail());
		mailMessage.setFrom("useradmin@ksug.org");
		mailMessage.setSubject("Upgrade 안내");
		mailMessage.setText(user.getName() + "님의 등급이 " + user.getLevel().name() + "로 업그레이드 되었습니다!");
		
		this.mailSender.send(mailMessage);
		/*Properties props = new Properties();
		props.put("mail.smtp.host", "mail.ksug.org");
		Session s = Session.getInstance(props, null);
		
		MimeMessage message = new MimeMessage(s);
		try {
			message.setFrom(new InternetAddress("useradmin@ksug.org"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
			message.setSubject("Upgrade 안내");
			message.setText("사용자님의 등급이 " + user.getLevel().name() + " 로 업그레이드 되었습니다!");
			
			Transport.send(message);
		} catch (AddressException e) {
			throw new RuntimeException(e);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}*/
	}

	public void add(User user) {
		if(user.getLevel() == null) {
			user.setLevel(Level.BASIC);
		}
		userDao.add(user);
	}

	@Override
	public User get(String id) {
		return userDao.get(id);
	}

	@Override
	public List<User> getAll() {
		return userDao.getAll();
	}

	@Override
	public void deleteAll() {
		userDao.deleteAll();
	}

	@Override
	public void update(User user) {
		userDao.update(user);
	}
}
