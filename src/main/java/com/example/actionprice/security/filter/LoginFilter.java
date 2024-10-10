package com.example.actionprice.security.filter;

import com.example.actionprice.user.forms.UserLoginForm;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.InputStreamReader;
import java.io.Reader;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

/**
 * 로그인 필터
 * @author : 연상훈
 * @created : 2024-10-06 오후 3:14
 * @updated : 2024-10-06 오후 3:14
 * @see : 책에서 약간 변형.
 */
@Log4j2
public class LoginFilter extends AbstractAuthenticationProcessingFilter {

  public LoginFilter(String defaultFilterProcessesUrl) {
    super(defaultFilterProcessesUrl);
  }

  /**
   * 사용자 로그인 시 해당 정보를 받아서 인증 토큰에 입력하는 기능.
   * @author : 연상훈
   * @created : 2024-10-06 오후 5:50
   * @updated : 2024-10-06 오후 5:50
   * @see : 책과 달리 UserLoginForm을 활용해서 입력하는 것이 보다 직관적이고, 깔끔하다고 판단해서 그렇게 수정
   */
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    log.info("[class] LoginFilter - [method] attemptAuthentication > 시작");

    if(request.getMethod().equalsIgnoreCase("GET")) {

      log.info("GET method doesn't supported");

      return null;
    }

    UserLoginForm loginForm = parseRequestJSON(request, UserLoginForm.class);

    log.info(loginForm);

    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword());

    log.info("[class] LoginFilter - [method] attemptAuthentication > authenticationToken : " + authenticationToken);

    // rememberMe의 기본적인 기능은 CustomSecurityConfig에서 구현되어 있지만, 추가적인 로직이 필요하다면 이걸로 구현 가능
//    if (loginForm.isRememberMe()) {
//    }

    return getAuthenticationManager().authenticate(authenticationToken);
  }

  /**
   * 요청의 내용을 json 형태로 변환하는 메서드
   * @author : 연상훈
   * @created : 2024-10-06 오후 5:56
   * @updated : 2024-10-06 오후 5:56
   * @see : 책과 달리 UserLoginForm을 사용하기 때문에 데이터를 UserLoginForm 형태에 맞추어 변환하고 반환함.
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
