package com.example.actionprice.sendEmail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

/**
 * @author 연상훈
 * @created 24/10/01 22:50
 * @updated 24/10/02 11:46
 * @value senderEmail = 보낼 사람의 이메일. properties에 등록되어 있음. 현재 연상훈 이메일
 * @value javaMailSender = 자바에서 공식적으로 지원하는 이메일 발송 클래스.
 * @info @info api의 간편한 재사용을 위해 SendEmailComponent 클래스를 만들어서 @Component 로 등록하여 중복되는 객체 생성을 피하고, 메모리 사용을 줄임. 그리고 내장된 메서드를 통해 값을 반환함.
 */
@Component
@RequiredArgsConstructor
public class SendEmailComponent {
		
	@Value("${spring.mail.username}")
	private String senderEmail;

	private final JavaMailSender javaMailSender;

	/**
	 * @author 연상훈
	 * @created 24/10/01 22:50
	 * @updated 24/10/02 11:46
	 * @param : receiverEmail = 받는 사람의 이메일
	 * @param : subject = 보낼 이메일의 제목
	 * @param : content = 보낼 이메일의 내용
	 * @throws Exception
	 * @info 간단 이메일 발송 로직. 오류를 대충 처리했음
	 */
	public boolean sendSimpleMail(String receiverEmail, String subject, String content) throws Exception{
		
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setFrom(senderEmail);
		simpleMailMessage.setTo(receiverEmail);
		simpleMailMessage.setSubject(subject);
		simpleMailMessage.setText(content);
		
		try {
			javaMailSender.send(simpleMailMessage);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;		
	}

	/**
	 * @author 연상훈
	 * @created 24/10/01 22:50
	 * @updated 24/10/02 11:46
	 * @param : receiverEmail = 받는 사람의 이메일
	 * @param : subject = 보낼 이메일의 제목
	 * @param : content = 보낼 이메일의 내용
	 * @throws Exception
	 * @info 간단 이메일 발송 로직. 오류를 대충 처리했음
	 */
	public boolean sendMimeMail(String receiverEmail, String subject, String content) throws Exception{
		MimeMessage message = javaMailSender.createMimeMessage();
		
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, false, "UTF-8");
		mimeMessageHelper.setFrom(senderEmail);
		mimeMessageHelper.setTo(receiverEmail); // 메일 수신자
		mimeMessageHelper.setSubject(subject); // 메일 제목
		mimeMessageHelper.setText(content); // 메일 본문 내용, HTML 여부
		
		try {
            javaMailSender.send(message);
        } catch (Exception e) {
        	e.printStackTrace();
			return false;
		}

		return true;
	}

}
