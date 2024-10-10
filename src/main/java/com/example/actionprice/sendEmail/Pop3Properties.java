package com.example.actionprice.sendEmail;

import jakarta.mail.Session;
import jakarta.mail.Store;
import java.util.Properties;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 이메일 발송 후 잘못된 이메일로 보내졌는지 체크하기 위한 일종의 컴포넌트
 * @author : 연상훈
 * @created : 2024-10-11 오전 12:16
 * @updated : 2024-10-11 오전 12:16
 * @see : 
 * 원래 컴포넌트로 하려 했는데, 그랬다간 @Value를 엄청 많이 씀
 * config로 하고, @ConfigurationProperties(prefix = "mail.pop3s")을 붙이면 훨씬 간결해져서
 * 컴포넌트 대신 config로 함
 * 다른 건 몰라도 username과 password는 보안이 필요한 사항이기 때문에
 * 외부에서 이 값을 가져다 쓸 수 없도록 @Getter를 쓰지 않고, 필요한 필드값들만 별도의 get 메서드 구현
 */
@Configuration
@ConfigurationProperties(prefix = "mail.pop3s") // 이거 덕에 @Value 안 써도 됨
@Setter
public class Pop3Properties {

  private String host;
  private String port;
  private String folder;
  private String username;
  private String password;
  private int untilTime;

  public String getFolder() {
    return folder;
  }

  public int getUntilTime() {
    return untilTime;
  }

  public Store getPop3Store() throws Exception {
    Properties properties = new Properties();
    properties.put("mail.store.protocol", "pop3s");
    properties.put("mail.pop3s.host", host);
    properties.put("mail.pop3s.port", port);
    properties.put("mail.pop3s.auth", "true");
    properties.put("mail.pop3s.starttls.enable", "true");
    properties.put("mail.pop3s.ssl.trust", "*");
    properties.put("mail.pop3s.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

    Session session = Session.getInstance(properties);
    Store store = session.getStore("pop3s");
    store.connect(username, password);
    return store;
  }

}
