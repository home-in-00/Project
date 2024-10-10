package com.example.actionprice.user;

import com.example.actionprice.exception.UsernameAlreadyExistsException;
import com.example.actionprice.user.forms.UserRegisterForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : 연상훈
 * @created : 2024-10-06 오후 9:17
 * @updated : 2024-10-10 오전 11:07
 * @see :
 */
@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * @author 연상훈
   * @created 2024-10-10 오전 11:05
   * @updated 2024-10-10 오전 11:05
   * @see :
   * src/main/java/com/example/actionprice/exception/UsernameAlreadyExistsException.java
   * src/main/java/com/example/actionprice/advice/CustomRestAdvice.java
   */
  @Override
  public User createUser(UserRegisterForm userRegisterForm) {
    log.info("--------------- [UserService] createUser ----------------");
    log.info("userRegisterForm: " + userRegisterForm);

    String inputed_username = userRegisterForm.getUsername();
    log.info("inputed_username: " + inputed_username);
    User existing_user = userRepository.findById(inputed_username).orElse(null);

    // 이미 존재하는 유저라면
    if(existing_user != null) {
      log.info(inputed_username + " already exists");
      throw new UsernameAlreadyExistsException("[username : " + inputed_username + "] already exists");
    }

    log.info(userRegisterForm.getUsername() + " is new user");
    // user 구성
    User newUser = User.builder()
        .username(userRegisterForm.getUsername())
        .password(passwordEncoder.encode(userRegisterForm.getPassword()))
        .email(userRegisterForm.getEmail())
        .build();

    // 권한은 일반 유저
    newUser.addAuthorities("ROLE_USER");

    // 저장
    userRepository.save(newUser);

    log.info(newUser.getUsername() + "register successful");

    return newUser;
  }

  /**
   * 유저 로그인 기능
   * @author : 연상훈
   * @created : 2024-10-06 오후 9:17
   * @updated : 2024-10-06 오후 9:17
   * @see : 로그인 기능은 CustomSecurity와 LoginFilter로 처리하기 때문에 별도로 사용할 필요가 없음.
   */

  /**
   * 유저 로그아웃 기능
   * @author 연상훈
   * @created 2024-10-10 오전 10:23
   * @updated 2024-10-10 오전 10:23
   * @see : 로그아웃 기능은 CustomSecurity로 처리하기 때문에 별도로 사용할 필요가 없음.
   */

  /**
   * 해당 username을 가진 사용자가 존재하는지 체크하는 메서드.
   * @author 연상훈
   * @created 2024-10-10 오전 10:25
   * @updated 2024-10-10 오전 10:25
   * @see :
   * 존재하면 true / 존재하지 않으면 false 반환
   * 재사용 가능성이 높은 메서드인 만큼, 간단하게 username만 입력 받도록 구성
   */
  @Override
  public boolean checkUserExists(String username) {

    log.info("--------------- [UserService] checkUserExists ----------------");

    log.info("inputed_username: " + username);
    User existing_user = userRepository.findById(username).orElse(null);

    if(existing_user != null) {
      return true;
    }

    return false;
  }
}
