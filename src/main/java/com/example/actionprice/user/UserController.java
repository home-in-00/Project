package com.example.actionprice.user;

import com.example.actionprice.sendEmail.SendEmailService;
import com.example.actionprice.user.forms.UserRegisterForm;
import com.example.actionprice.user.forms.UserRegisterForm.CheckForDuplicateUsernameGroup;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO exception 처리를 구체화할 필요가 있음
/**
 * @author : 연상훈
 * @created : 2024-10-05 오후 10:52
 * @updated : 2024-10-12 오전 00:59
 * @see :
 * 1. @CustomRestAdvice - handlerBindException로 유효성 검사 오류 처리하고 있으니 별도로 할 필요가 없음
 */
@RestController
@RequestMapping("/api/user")
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
   * @PostMapping("/login") 로그인 기능
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
   * @GetMapping("/logout") & @PostMapping("/logout") 로그아웃 기능
   * @author 연상훈
   * @created 2024-10-10 오전 9:30
   * @updated 2024-10-10 오전 9:30
   * @see CustomSecurityConfig에서 처리하기 때문에 별도의 메서드가 필요 없음.
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
   * @updated : 2024-10-10 오전 11:07
   * @see :
   * UserRegisterForm을 사용해야 함
   * username 중복 시 UsernameAlreadyExistsException 처리
   * src/main/java/com/example/actionprice/exception/UsernameAlreadyExistsException.java
   * src/main/java/com/example/actionprice/advice/CustomRestAdvice.java
   */
  @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, Map<String, String>>> register(@Valid @RequestBody UserRegisterForm form) {
    // 유효성 검사는 @CustomRestAdvice가 자동으로 처리함
    userService.createUser(form);
    String message = String.format("%s 님의 회원가입이 성공적으로 이루어졌습니다.", form.getUsername());
    Map<String, Map<String, String>> response = new HashMap<>();
    Map<String, String> data = new HashMap<>();
    data.put("message", message);
    response.put("data", data);
    return ResponseEntity.ok(response);
  }

  /**
   * 회원가입 시 인증코드 발송 기능
   * @author : 연상훈
   * @created : 2024-10-06 오후 8:24
   * @updated : 2024-10-11 오후 21:41
   * @see : UserRegisterForm을 사용해야 함
   * 검증 그룹으로 SendVerificationCodeGroup을 사용합니다.
   * 내부적으로 InvalidEmailAddressException을 사용합니다. 존재하지 않는 이메일로 발송 시 에러를 일으키니, 그에 대한 처리가 필요합니다.
   * https://www.knotend.com/g/a#N4IgzgpgTglghgGxgLwnARgiAxA9lAWxAC5QA7XAEwjBPIEYAmRgVkYBZ6AOOkBDCAhIhALuOAVLsA+4wAJAOD2BCwZABfADQgy7AJwAGAMwA2DU1790g4SMAf3bLmAcFqniJIqYBcJwDXjAfiWqyAdh1cNDUYfLWMBIWIQAHkvNQ0Weno9PXYfMNMIkHk7QAHJqRZAEN6pHMAKhs8VNXp2FnZgrRZ0s0iADViyYJ16Qy4G0j5w4RiK9RZAxK0ePpMmkFbh0a4fdi4jKYHIwAmBwB0O2UBIOqlAEbX7SWs2vS16H0WORszABjrnKUAHGqlAB5HAH3bAA5qpQAtVtsCND4fIleqBpplACATcikgEZBwCvNeVvPF2OwdEC9LdhAAzRCQNo9apAiaYyJDbxAnSseok2ZtHRsDQpPT0GnwqRiQA7Qzk2okDHpGIyaQ4pIAM8cA3V2ACha2lclnp9OwaQAXKAAVwgdJYWkYXD0aTWGWEEAAHgBjCAAB0VMFwZCkgAyZwA1nUoKhBKABzGgkADaXsqzDYnB43k0ugMRgAuspfeptPpDIwQOT-IE6iBI9GQ3GjN4qjU6g102o-AEgiFE3EEkkUmlC2R4rzq+X2n4utwC1GKw3Uk3qmN6MTa7naiEGt4FksVgnax1Wz0m+PlhGOyM+8TvOdLtcFbWF5OmwCgSC08uNzKbkiaqj0cfowfgfRR2oCSwiTxa3ej+S-FT29Hn6+m3pAUmRZWsKR-JteUZYCbzUIDGXYZkmxlRD5VgsgoP5QVvHpLUdRrcMKjNBAEAAdRgShFQACxIeh6VUYiEAACQgGA3SoxUSGWBjBAQAAFOBKEoGAyDdEh6LUKgICiKBqFgUTvT9VgOG4HtYzDBMkxLVMLy7NIc2qYdqW8GdukfFdDH7IM1F3bM1FPLd90CQ8H33S80R8DFvH-EJrN8b9NXM+CQMgpJoOwotFlQlJAM1bVdTTRQgA
   */
  @PostMapping(value = "/sendVerificationCode", consumes = MediaType.APPLICATION_JSON_VALUE) //요청을 json 타입으로 받음
  public ResponseEntity<String> sendVerificationCode(@Validated(SendVerificationCodeGroup.class) @RequestBody UserRegisterForm userRegisterForm) throws Exception {

    // 유효성 검사는 @CustomRestAdvice가 자동으로 처리함

    String email = userRegisterForm.getEmail();

    //이미 사용하고 있는 이메일인지 체크하는 로직
    if(userService.checkUserExistsWithEmail(email)){
      log.info("[class] UserController - [method] sendVerificationCode - email already used");
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already used");
    }

    // 발송되었으면 true, 이미 5분 내로 발송된 것이 있으면 false.
    // 이미 발송된 것이 있지만 5분이 지났으면 새로 발송해주고 true 반환.
    // 발송 실패 시 InvalidEmailAddressException으로 처리
    boolean isEmailSent = sendEmailService.sendVerificationEmail(email);

    String resultOfSending = isEmailSent ? "인증코드가 성공적으로 발송되었습니다." : "최근 5분 내로 이미 발송된 인증코드가 있습니다. 발송된 코드를 사용해주세요.";
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
  public ResponseEntity<String> checkVerificationCode(@Valid @RequestBody UserRegisterForm form){

    // 유효성 검사는 @CustomRestAdvice가 자동으로 처리함

    String resultOfVerification = sendEmailService.checkVerificationCode(form.getEmail(), form.getVerificationCode());
    return ResponseEntity.ok(resultOfVerification);
  }

  /**
   * @author 연상훈
   * @created 2024-10-10 오전 11:16
   * @updated 2024-10-10 오후 16:31
   * @endPoint : /api/user/checkForDuplicateUsername
   * @see :
   * 검증그룹으로 CheckForDuplicateUsernameGroup 사용
   * 이곳은 register와 달리 에러 처리 없이 그대로 감
   */
  @PostMapping(value = "/checkForDuplicateUsername", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> checkForDuplicateUsername(@Validated(CheckForDuplicateUsernameGroup.class) @RequestBody UserRegisterForm form){

    // 유효성 검사는 @CustomRestAdvice가 자동으로 처리함

    log.info("[class] UserController - [method] checkForDuplicateUsername - operate");

    boolean useranme_already_exist = userService.checkUserExistsWithUsername(form.getUsername());

    // userService.checkUserExistsWithUsername()는 존재하면 true, 존재하지 않으면 false 반환
    if (useranme_already_exist) {
      log.info("[class] UserController - [method] checkForDuplicateUsername - Username already exists");
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
    }

    log.info("[class] UserController - [method] checkForDuplicateUsername - new username");
    return ResponseEntity.ok("Username is available");
  }

}
