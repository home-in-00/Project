package com.example.actionprice.user;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
@Log4j2
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  public void userCreateTest() {
    User user = User.builder()
        .username("useruser")
        .password(passwordEncoder.encode("11111111"))
        .email("bscom129@naver.com")
        .build();

    user.addAuthorities(UserRole.USER.name());

    userRepository.save(user);
  }

}
