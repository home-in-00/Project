package com.example.actionprice.user;

import com.example.actionprice.sendEmail.SendEmailService;
import com.example.actionprice.user.forms.UserRegisterForm;
import com.example.actionprice.user.forms.UserRegisterForm.CheckVerificationCodeGroup;
import com.example.actionprice.user.forms.UserRegisterForm.SendVerificationCodeGroup;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : 연상훈
 * @created : 2024-10-05 오후 10:52
 * @updated : 2024-10-06 오후 6:26
 * @see : 단순한 페이지 이동 기능만 구현하였습니다.
 */
@RestController
@RequestMapping("/user")
@Log4j2
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final SendEmailService sendEmailService;

  /**
   * 로그인 페이지로 이동
   * @author : 연상훈
   * @created : 2024-10-06 오후 6:33
   * @updated : 2024-10-06 오후 6:33
   * @see : 단순한 이동이기에 별도의 로직이 필요 없지만, 페이지 간 이동의 요청을 명확히 하기 위해 구현
   */
  @GetMapping("/login")
  public ResponseEntity<Map<String, String>> goLogin(){
    Map<String, String> response = new HashMap<>();
    response.put("url", "/user/login");
    return ResponseEntity.ok(response);
  }

  /**
   * @PostMappin("/user/login") 로그인 기능
   * @author : 연상훈
   * @created : 2024-10-06 오후 6:35
   * @updated : 2024-10-06 오후 6:35
   * @see :
   * 로그인 로직에 해당하는 @PostMappin("/user/login")은
   * CustomSecurityConfig에서 처리하기 때문에 별도의 메서드가 필요 없음.
   * 오히려 만들었다간 요청 충돌이 생김.
   * 참고로 user login 할 때 UserLoginForm을 사용함
   */

  /**
   * 회원가입 페이지로 이동
   * @author : 연상훈
   * @created : 2024-10-06 오후 6:33
   * @updated : 2024-10-06 오후 6:33
   * @see : 단순한 이동이기에 별도의 로직이 필요 없지만, 페이지 간 이동의 요청을 명확히 하기 위해 구현
   */
  @GetMapping("/register")
  public ResponseEntity<Map<String, String>> goRegister(){
    Map<String, String> response = new HashMap<>();
    response.put("url", "/user/register");
    return ResponseEntity.ok(response);
  }

  /**
   * 회원가입 기능
   * @author : 연상훈
   * @created : 2024-10-06 오후 8:26
   * @updated : 2024-10-06 오후 8:26
   * @see : UserRegisterForm을 사용해야 함
   */
  @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> register(@Valid @RequestBody UserRegisterForm form, BindingResult bindingResult) {
    // 조건에 맞게 입력했는지 체크
    if (bindingResult.hasErrors()) {
      // 맞지 않으면 에러
      String errorMessage = bindingResult.getFieldError().getDefaultMessage();
      return ResponseEntity.badRequest().body(errorMessage);
    }

    // 조건을 통과했으면 유저 객체 생성
    userService.createUser(form);
    // 그리고 "/user/login"로 리다이렉트
    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create("/user/login"))
        .build();
  }

  /**
   * 회원가입 시 인증코드 발송 기능
   * @author : 연상훈
   * @created : 2024-10-06 오후 8:24
   * @updated : 2024-10-06 오후 8:24
   * @see : UserRegisterForm을 사용해야 함
   * 검증 그룹으로 SendVerificationCodeGroup을 사용합니다.
   */
  @PostMapping(value = "/sendVerificationCode", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> sendVerificationCode(@Validated(SendVerificationCodeGroup.class) @RequestBody UserRegisterForm form, BindingResult bindingResult){
    // 이메일 유효성 검사 후 처리
    if (bindingResult.hasErrors()) {
      return ResponseEntity.badRequest().body(bindingResult.getFieldError().getDefaultMessage());
    }

    String email = form.getEmail();
    String resultOfSending = sendEmailService.sendVerificationEmail(email);
    return ResponseEntity.ok(resultOfSending);
  }

  /**
   * 회원가입 시 인증코드 검증 기능
   * @author : 연상훈
   * @created : 2024-10-06 오후 8:24
   * @updated : 2024-10-06 오후 8:24
   * @see : UserRegisterForm을 사용해야 함
   * 검증 그룹으로 CheckVerificationCodeGroup 사용합니다.
   */
  @PostMapping(value = "/checkVerificationCode", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> checkVerificationCode(@Validated(CheckVerificationCodeGroup.class) @RequestBody UserRegisterForm form, BindingResult bindingResult){
    // 이메일과 인증 코드 유효성 검사 후 처리
    if (bindingResult.hasErrors()) {
      return ResponseEntity.badRequest().body(bindingResult.getFieldError().getDefaultMessage());
    }

    String email = form.getEmail();
    String verificationCode = form.getVerificationCode();
    String resultOfVerification = sendEmailService.checkVerificationCode(email, verificationCode);
    return ResponseEntity.ok(resultOfVerification);
  }

}
