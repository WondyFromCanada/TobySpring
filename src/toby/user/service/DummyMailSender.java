package toby.user.service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class DummyMailSender implements MailSender{

	@Override
	public void send(SimpleMailMessage mailMessage) throws MailException {
		System.out.println("메일 발송자 : " + mailMessage.getFrom());
		System.out.print("메일 수신자 : ");
		for(String receiver : mailMessage.getTo()) {
			System.out.println(receiver);
		}
		/*for(int i = 0; i < mailMessage.getTo().length; i++) {
			System.out.println(mailMessage.getTo()[i]);
		}*/
		System.out.println("메일 제목    : " + mailMessage.getSubject());
		System.out.println("메일 내용    : " + mailMessage.getText());
		System.out.println("----------------------------------------");
	}

	@Override
	public void send(SimpleMailMessage[] mailMessage) throws MailException {

	}
}