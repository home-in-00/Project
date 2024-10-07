package com.example.actionprice.user;

import com.example.actionprice.user.forms.UserRegisterForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : 연상훈
 * @created : 2024-10-06 오후 9:17
 * @updated : 2024-10-06 오후 9:17
 */
@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public User createUser(UserRegisterForm userRegisterForm) {
    String inputed_username = userRegisterForm.getUsername();
    User existing_user = userRepository.findById(inputed_username).orElse(null);

    // 이미 존재하는 유저라면
    if(existing_user != null) {
      return existing_user; // exception이나 뭐로 던져버릴까? 그냥 반환하는 건 좀 이상한데
    }

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

    return newUser;
  }

  /**
   * 유저 로그인 기능
   * @author : 연상훈
   * @created : 2024-10-06 오후 9:17
   * @updated : 2024-10-06 오후 9:17
   * @see : 로그인 기능은 CustomSecurity와 LoginFilter로 처리하기 때문에 별도로 사용할 필요가 없음.
   */
}
