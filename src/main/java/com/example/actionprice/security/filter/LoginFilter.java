package com.example.actionprice.security.filter;

import com.example.actionprice.exception.UserNotFoundException;
import com.example.actionprice.security.CustomUserDetailService;
import com.example.actionprice.user.User;
import com.example.actionprice.user.UserRepository;
import com.example.actionprice.user.forms.UserLoginForm;
import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDateTime;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.HttpRequestMethodNotSupportedException;

/**
 * 로그인 필터
 * @author 연상훈
 * @created 2024-10-06 오후 3:14
 * @updated
 * @value userDetailService
 * @value successHandler
 * @value userRepository
 */
@Log4j2
public class LoginFilter extends AbstractAuthenticationProcessingFilter {

  private final CustomUserDetailService userDetailService;
  private final AuthenticationSuccessHandler loginSuccessHandler;
  private final UserRepository userRepository;

  /**
   * LoginFilter 생성자
   * @author : 연상훈
   * @created : 2024-10-14 오전 5:57
   * @updated : 2024-10-14 오전 5:57
   * @info : 받아올 게 많아서 좀 긴 편
   */
  public LoginFilter(
      String defaultFilterProcessesUrl,
      CustomUserDetailService userDetailService,
      AuthenticationSuccessHandler loginSuccessHandler,
      UserRepository userRepository,
      AuthenticationManager authenticationManager
  ) {
    super(defaultFilterProcessesUrl);
    this.userDetailService = userDetailService;
    this.loginSuccessHandler = loginSuccessHandler;
    this.userRepository = userRepository;
    setAuthenticationSuccessHandler(loginSuccessHandler);
    setAuthenticationManager(authenticationManager);
  }

  /**
   * 사용자 로그인 시 해당 정보를 받아서 인증 토큰에 입력하는 기능.
   * @author 연상훈
   * @created 2024-10-06 오후 5:50
   * @updated 2024-10-17 오후 7:14 : 리멤버미 삭제
   * @info 리액트를 통해 받아오는 request는 request.getParameter 등을 쓸 수 없음.
   * 그래서 귀찮게 일일이 빼오거나 다른 객체로 변환하거나 둘 중 하나이고, 우리는 UserLoginForm 객체 사용함
   * @info userDetailService.loadUserByUsername에서 user 계정 lock 상태에 따라 잠금된 상태로 반환함. 그렇게 해서 계정 잠금 구현
   */
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException, HttpRequestMethodNotSupportedException {
    log.info("[class] LoginFilter - [method] attemptAuthentication > 시작");

    // get 요청으로 들어오면 거부함
    if(request.getMethod().equalsIgnoreCase("GET")) {

      log.info("GET method doesn't supported");

      throw new HttpRequestMethodNotSupportedException("GET");
    }
    
    UserLoginForm loginForm = parseRequestJSON(request, UserLoginForm.class);

    log.info(loginForm);

    String username = loginForm.getUsername();

    UserDetails userDetails = userDetailService.loadUserByUsername(username);
    log.info("CustomUserDetails : {}", userDetails);

    // 로그인 성공/실패 시에도 username값을 가지고 로직을 수행해야 해서 잠시 담아줌.
    // 성공 시에는 principal에서 받아올 수 있지만, 실패 시에는 아예 없음
    request.setAttribute("username", username);
    UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(userDetails, loginForm.getPassword(), userDetails.getAuthorities());
    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

    log.info("[class] LoginFilter - [method] attemptAuthentication > authenticationToken : " + authenticationToken);

    return getAuthenticationManager().authenticate(authenticationToken);
  }

  /**
   * 로그인 성공 시 실행되는 로직
   * @author 연상훈
   * @created 2024-10-14 오전 6:08
   * @info 로그인 성공 시 로그인 실패 횟수 초기화하고 잠금 기록 삭제
   */
  @Override
  protected void successfulAuthentication(
          HttpServletRequest request, 
          HttpServletResponse response, 
          FilterChain chain, 
          Authentication authResult
  ) throws ServletException, IOException {
    
    log.info("[class] LoginFilter - [method] successfulAuthentication > 로그인 성공");

    // 솔직히 여기까지 오면 오류 날 리가 없지만 그래도 오류 처리 해줌
    String username = (String)request.getAttribute("username");
    log.info("[class] LoginFilter - [method] successfulAuthentication > username : {}", username);
    if(username == null) {
      return;
    }

    User user = userRepository.findById(username)
            .orElseThrow(() -> new UserNotFoundException("user(" + username + ") does not exist"));

    // 로그인 성공하면 관련 기록 삭제
    user.setLoginFailureCount(0);
    user.setLockedAt(null);

    userRepository.save(user);

    log.info("[class] LoginFilter - [method] successfulAuthentication > 종료");
    loginSuccessHandler.onAuthenticationSuccess(request, response, authResult);
  }

  /**
   * 로그인 실패 시 실행되는 로직
   * @author : 연상훈
   * @created : 2024-10-14 오전 6:09
   * @info 계정 잠금으로 인한 로그인 실패 시 메시지만 출력하고 곱게 보내줌
   * @info 아니면 username 조사 실행
   * @info - username 조사 결과, 존재하지 않는 username이면, username 틀림 출력 후 보내줌
   * @info - username 조사 결과, username이 존재한다면, 비밀번호가 틀린 것. 로그인 실패 횟수 조사 실행
   * @info - 로그인 실패 횟수 조사 결과, 5회 이상 실패했으면 계정을 5분 간 잠금처리하고 실패횟수 초기화
   * @info - 로그인 실패 횟수 조사 결과, 5회 미만 실패했으면 실패횟수 하나 추가
   */
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed) throws IOException {
    log.info("[class] LoginFilter - [method] unsuccessfulAuthentication > 로그인 실패");

    // 계정 잠금 상태인 걸로 실패한 거면 빠르게 끝냄
    if(failed instanceof LockedException){
      log.info("계정 잠금 상태입니다.");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().println("[loginFailure] Your account have been locked for 5 minutes");
      log.info("[class] LoginFilter - [method] unsuccessfulAuthentication > 종료");
      return;
    }

    String username = (String)request.getAttribute("username");
    log.info("[class] LoginFilter - [method] successfulAuthentication > username : {}", username);
    if(username == null) {
      return;
    }

    User user = userRepository.findById(username).orElse(null);
    if(user == null) {
      log.info("존재하지 않는 사용자입니다.");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().println("[loginFailure] Incorrect username or password");
      log.info("[class] LoginFilter - [method] unsuccessfulAuthentication > 종료");
      return;
    }

    log.info("존재하는 사용자입니다.");

    int loginFailureCount = user.getLoginFailureCount()+1;
    String responseMessage = "[loginFailure] Incorrect username or password";

    // 실패횟수가 5 이상이면
    if(loginFailureCount >= 5) {
      log.info("실패횟수가 5회 이상입니다. 계정 잠금 처리 및 실패횟수 초기화를 진행합니다.");
      // 계정 잠금처리
      user.setLockedAt(LocalDateTime.now());
      // 실패횟수를 놔두면 무한히 잠금 시간이 갱싱될 수 있으니 초기화 해줌
      // 그런데 이 상태에서 또 5번 틀려서 갱신되면 어쩔 수 없는 거고
      loginFailureCount = 0;
    }

    user.setLoginFailureCount(loginFailureCount);
    userRepository.save(user);
    log.info("로그인 실패 횟수를 갱신합니다. 현재 {}회입니다.", loginFailureCount);

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().println(responseMessage);
    log.info("[class] LoginFilter - [method] unsuccessfulAuthentication > 종료");
  }

  /**
   * 요청의 내용을 json 형태로 변환하는 메서드
   * @author : 연상훈
   * @created : 2024-10-06 오후 5:56
   * @info UserLoginForm을 사용하기 때문에 데이터를 UserLoginForm 형태에 맞추어 변환하고 반환함.
   */
  private <T> T parseRequestJSON(HttpServletRequest request, Class<T> classObj) {
    try (Reader reader = new InputStreamReader(request.getInputStream())) {
      Gson gson = new Gson();
      return gson.fromJson(reader, classObj);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return null;
  }
}
