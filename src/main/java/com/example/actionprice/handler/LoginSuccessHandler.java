package com.example.actionprice.handler;

import com.example.actionprice.user.UserRepository;
import com.example.actionprice.util.JWTUtil;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

// TODO admin 페이지 만들고, 블랙리스트 관리하기
// TODO exception 처리를 구체화할 필요가 있음
/**
 * 로그인 성공 시 사용되는 핸들러
 * @author : 연상훈
 * @created : 2024-10-06 오후 3:26
 * @updated : 2024-10-06 오후 3:26
 * @see : 책대로 함
 */
@Log4j2
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JWTUtil jwtUtil;
  private final UserRepository userRepository;

  /**
   * 인증이 성공하면 진행되는 절차
   * @author : 연상훈
   * @created : 2024-10-06 오후 6:17
   * @updated : 2024-10-06 오후 6:17
   * @see : 토큰 유효기간 재설정 필요.
   */
  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) throws IOException
  {

    log.info("----------- Login Success Handler -----------");

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    log.info(authentication + " : " + authentication.getName());

    Map<String, Object> claim = Map.of("username", authentication.getName()); // 키 : username, 밸류 : 인증된 사용자 네임
    log.info("claim : " + claim);
    String accessToken = jwtUtil.generateToken(claim, 60); // 생성한 클레임(맵)으로 엑세스토큰 발행
    String refreshToken = jwtUtil.generateToken(claim, 60 * 3); // 리프레쉬 토큰 발행

    // 추후 리프레시토큰을 통한 블랙리스트 관리를 위한 로직. 하지만 그건 admin 페이지가 생기고 나서야 가능함.
//    String username = authentication.getName();
//
//    User user = userRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException("유저[" + username + "]가 존재하지 않습니다."));
//    user.setRefreshToken(refreshToken);
//    userRepository.save(user);

    Gson gson = new Gson();

    Map<String, String> keyMap = Map.of("access_token", accessToken,"refresh_token", refreshToken, "username", authentication.getName()); // map 형태로 토큰 정보를 저장

    String jsonStr = gson.toJson(keyMap); // json에 토큰 정보를 전달

    log.info("Login Success Handler : " + jsonStr);

    response.getWriter().println(jsonStr); // response(json 형태)의 writer에 토큰 정보 추가
  }
}
