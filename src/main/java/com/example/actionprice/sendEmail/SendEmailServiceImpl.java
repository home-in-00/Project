package com.example.actionprice.sendEmail;

import com.example.actionprice.exception.InvalidEmailAddressException;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Store;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

// TODO exception 처리를 구체화할 필요가 있음
/**
 * @author 연상훈
 * @created 24/10/01 22:50
 * @updated 24/10/12 00:20
 * @value : senderEmail = 보낼 사람의 이메일. properties에 등록되어 있음. 현재 연상훈 이메일
 * @value : javaMailSender = 자바에서 공식적으로 지원하는 이메일 발송 클래스.
 * @value : pop3Properties = 이메일 발송 후 잘못된 이메일로 보내졌는지 체크하기 위한 일종의 컴포넌트(형식은 config)
 */
@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class SendEmailServiceImpl implements SendEmailService {
		
	@Value("${spring.mail.username}")
	private String senderEmail;

	private final JavaMailSender javaMailSender;
	private final VerificationEmailRepository verificationEmailRepository;
	private final Pop3Properties pop3Properties;

	// 무작위 문자열을 만들기 위한 준비물
	private final SecureRandom random = new SecureRandom();
	private final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private final int CODE_LENGTH = 8;

	/**
	 * 회원가입 시 이메일 인증을 위한 인증 코드 발송 메서드
	 * @author 연상훈
	 * @created 2024-10-10 오전 11:13
	 * @updated 2024-10-10 오후 4:31
	 * @see :
	 */
	@Override
	public boolean sendVerificationEmail(String email) throws Exception {

		// 해당 이메일로 발급 받은 verificationEmail이 있으면 가져오고, 없으면 null 반환
		VerificationEmail verificationEmail = verificationEmailRepository.findById(email).orElse(null);

		String verificationCode = generateRandomCode();

		if (verificationEmail != null) { // 이미 존재하는지 체크
			// 이미 있으면, 생성된 지 5분 이상 지났는지 체크
			if (ChronoUnit.MINUTES.between(verificationEmail.getCreatedAt(), LocalDateTime.now()) > 5) {
				verificationEmail.setVerificationCode(verificationCode);
			} else{
				log.info("최근에 발송된 인증코드가 존재합니다."); // 5분이 지나지 않았으면, 다시 보내지 않고 그냥 발송된 것으로 넘어감
				return false;
			}
		} else {
			// 존재하지 않는 경우 새로 생성
			verificationEmail = VerificationEmail.builder()
					.email(email)
					.verificationCode(verificationCode)
					.build();
		}

		// 이메일 내용 구성.
		String subject = "[actionPrice] 회원가입 인증코드입니다.";
		String content = String.format("""
						인증코드
						-------------------------------------------
						%s
						-------------------------------------------
						""", verificationCode);

		sendSimpleMail(email, subject, content); // 이메일 발송. 잘못된 이메일로 발송 시 자동으로 예외 처리

		verificationEmailRepository.save(verificationEmail);

		return true;
	}

	@Override
	public String checkVerificationCode(String email, String verificationCode) {
		VerificationEmail verificationEmail = verificationEmailRepository.findById(email).orElse(null);

		// 잘못된 이메일이면
		if (verificationEmail == null) {
			return "잘못된 이메일을 입력하셨습니다.";
		}

		// 정상적인 이메일이면
		// 인증 코드 유효 시간 체크
		if (ChronoUnit.MINUTES.between(verificationEmail.getCreatedAt(), LocalDateTime.now()) < 5) {

			// 유효시간이 지나지 않았으면
			// 인증코드 일치하는지 확인
			if (!verificationEmail.getVerificationCode().equals(verificationCode)) {
				// 불일치
				return "인증코드가 일치하지 않습니다. 다시 입력해주세요.";
			}

			// 일치
			verificationEmailRepository.delete(verificationEmail); // 인증 성공해서 더이상 필요 없으니 삭제
			return "인증이 성공했습니다."; // 인증 성공

		} else { // 유효시간이 지났으면
			verificationEmailRepository.delete(verificationEmail); // 만료된 인증 코드 삭제
			return "인증코드가 만료되었습니다. 다시 진행해주세요."; // 만료된 코드로 인증 실패
		}
	}

	/**
	 * 이메일 발송이 완료되었는지 확인하는 메서드
	 * @author : 연상훈
	 * @created : 2024-10-10 오후 9:44
	 * @updated : 2024-10-10 오후 11:57
	 * @see :
	 * 이메일 발송도 시간이 오래 걸리는데, 발송이 완료되고 나서 이것도 5초를 기다리면 너무 오래 걸리니까
	 * @Async로 비동기 처리하여 발송과 동시에 메서드 시작
	 * 그리고 store와 folder를 try() 안에 넣어서 try가 실패하면 자연스럽게 닫히게 함
	 */
	@Async
	public CompletableFuture<Boolean> isCompleteSentEmail(String email) throws Exception {
		log.info("이메일 발송이 완료되었는지 확인을 시작합니다. 먼저, 이메일 발송까지 5초 간 대기합니다.");

		// 이메일 발송까지 5초 간 대기
		Thread.sleep(5000);

		try (Store store = pop3Properties.getPop3Store()) {
			if (store.isConnected()) {
				store.close(); // 이미 연결된 경우 연결 닫기
			}

			store.connect(); // POP3 서버에 연결

			// 지정한 이메일 폴더 열기
			try (Folder emailFolder = store.getFolder(pop3Properties.getFolder())) {
				emailFolder.open(Folder.READ_ONLY); // 읽기 전용으로 폴더 열기

				log.info("이메일 폴더를 개방합니다.");

				// 현재 시간에서 지정된 시간만큼 이전 시간 계산
				Instant untilTime = Instant.now().minusSeconds(pop3Properties.getUntilTime());
				log.info("1분 내로 반송된 이메일이 있는지 검색합니다.");

				// 폴더 내 모든 메일 메시지 검색
				for (Message message : emailFolder.getMessages()) {
					// 메일의 발송 날짜가 지정된 시간 이내인지 확인
					if (untilTime.isBefore(message.getSentDate().toInstant())) {
						// 반송된 이메일 확인
						String[] failedRecipients = message.getHeader("X-Failed-Recipients");
						if (failedRecipients != null && Arrays.asList(failedRecipients).contains(email)) {
							log.info("[{}]로 전송한 이메일이 반송되었습니다.", email);
							try {
								// 반송된 이메일 삭제
								message.setFlag(Flags.Flag.DELETED, true);
							} catch (MessagingException e) {
								log.error("반송 이메일 삭제 중 에러 발생. error : {}", e.getMessage());
							}
							return CompletableFuture.completedFuture(false);
						}
					}
				}
			} // Folder 자동으로 닫힘
		} // Store 자동으로 닫힘

		log.info("반송된 이메일이 없으므로, true를 반환합니다.");
		return CompletableFuture.completedFuture(true);
	}

	/**
	 * 단순 이메일 발송 메서드
	 * @author 연상훈
	 * @created 24/10/01 22:50
	 * @updated 24/10/10 오후 4:31
	 * @param : receiverEmail = 받는 사람의 이메일
	 * @param : subject = 보낼 이메일의 제목
	 * @param : content = 보낼 이메일의 내용
	 * @throws :  Exception, InvalidEmailAddressException
	 * @info : 이 메서드 실행 중에 오류가 발생하면 자체적으로 InvalidEmailAddressException 으로 던지기 때문에 별도의 오류 처리가 필요 없음.
	 */
	@SneakyThrows
	public void sendSimpleMail(String receiverEmail, String subject, String content) throws InvalidEmailAddressException {
		log.info("이메일 전송 메서드 시작");
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setFrom(senderEmail);
		simpleMailMessage.setTo(receiverEmail);
		simpleMailMessage.setSubject(subject);
		simpleMailMessage.setText(content);

		javaMailSender.send(simpleMailMessage);
		log.info("[{}]로 인증코드를 발송합니다.", receiverEmail);

		try {
			isCompleteSentEmail(receiverEmail).get();  // 결과 대기 후 반환
		} catch (InterruptedException | ExecutionException e) {
			throw new InvalidEmailAddressException("[" + receiverEmail + "] does not exist");
		}
	}

	/**
	 * @author 연상훈
	 * @created 24/10/01 22:50
	 * @updated 24/10/02 11:46
	 * @param : receiverEmail = 받는 사람의 이메일
	 * @param : subject = 보낼 이메일의 제목
	 * @param : content = 보낼 이메일의 내용
	 * @throws : Exception
	 * @info 간단 이메일 발송 로직. 오류를 대충 처리했음
	 */
	public void sendMimeMail(String receiverEmail, String subject, String content) throws Exception{
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
		}
	}

	/**
	 * 인증코드 구성을 위한 랜덤한 8자리 문자열 생성 메서드
	 * @author : 연상훈
	 * @created : 2024-10-06 오후 7:42
	 * @updated : 2024-10-06 오후 7:42
	 */
	private String generateRandomCode(){
		StringBuilder code = new StringBuilder(CODE_LENGTH);
		for (int i = 0; i < CODE_LENGTH; i++) {
			int randomIndex = random.nextInt(CHARACTERS.length());
			code.append(CHARACTERS.charAt(randomIndex));
		}
		return code.toString();
	}

}
