package com.example.actionprice.security.filter;

import com.example.actionprice.exception.RefreshTokenException;
import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.actionprice.util.JWTUtil;

// TODO 토큰 유효시간 재설정
/**
 * 리프레시 토큰 체크 필터
 * @author : 연상훈
 * @created : 2024-10-06 오후 3:05
 * @updated : 2024-10-06 오후 3:05
 * @see : 책대로 함
 */
@Log4j2
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {

  // SecurityConfig에서 생성하면서 주입 받을 것들
  private final String refreshPath;
  private final JWTUtil jwtUtil;

  /**
   * 필터링 로직
   * @author : 연상훈
   * @created : 2024-10-06 오후 3:10
   * @updated : 2024-10-06 오후 3:10
   * @see : 책대로 함 
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String path = request.getRequestURI();

    if(!path.equals(refreshPath)){
      log.info("리프레시 토큰 필터를 위한 경로가 아니므로 넘어갑니다.");
      filterChain.doFilter(request, response);
      return;
    }

    log.info("리프레시 토큰 필터 실행");

    // 전송된 json에서 accessToken과 refreshToken을 얻어온다
    Map<String, String> tokens = parseRequestJSON(request);

    String access_token = tokens.get("access_token");
    String refresh_token = tokens.get("refresh_token");

    log.info("access_token : " + access_token);
    log.info("refresh_token : " + refresh_token);

    try{
      checkAccessToken(access_token);
    }
    catch(RefreshTokenException e){
      e.sendResponseError(response);
      return;
    }

    Map<String, Object> refreshClaims = null;

    try{
      refreshClaims = checkRefreshToken(refresh_token);
      log.info("현재 리프레시 토큰 상태 : " + refreshClaims.toString());

      // refresh token의 유효시간이 얼마 남지 않은 경우
      Integer exp = (Integer) refreshClaims.get("exp");

      // 밀리초 단위이기 때문에 1000 = 1초 / 1000 * 60 * 60 = 1시간
      Date current = new Date(System.currentTimeMillis());
      Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() * 1000 * 60 * 60);

      // 만료 시간과 현재 시간의 간격 계산
      // 만일 3일 미만인 경우에는 refresh token도 다시 생성
      long gapTime = (expTime.getTime() - current.getTime());

      log.info("--------------------------------");
      log.info("현재 시간 : " + current);
      log.info("만료 시간 : " + exp);
      log.info("남은 시간 : " + gapTime);

      String username = (String) refreshClaims.get("username");

      // 이 상태까지 오면 무조건 access token은 새로 생성
      // tokens의 시간은 분 단위임.
      String accessTokenValue = jwtUtil.generateToken(Map.of("username", username), 60);
      String refreshTokenValue = tokens.get("refresh_token");

      // refresh token도 3일도 안 남았다면
      if(gapTime < (60)){
        log.info("새로운 리프레시 토큰 발급이 필요합니다.");
        refreshTokenValue = jwtUtil.generateToken(Map.of("username", username), 60 * 3);
      }

      log.info("---------------- 현재 리프레시 토큰 값 ---------------- ");
      log.info("new access token: " + accessTokenValue);
      log.info("new refresh token: " + refreshTokenValue);
      log.info("--------------------------------");

      sendTokens(accessTokenValue, refreshTokenValue, response);
    }
    catch(RefreshTokenException e){
      e.sendResponseError(response);
      return;
    }
  }

  private Map<String, String> parseRequestJSON(HttpServletRequest request) {

    // json 데이터를 분석해서 username, mpw 전달 값을 Map으로 처리
    try(Reader reader = new InputStreamReader(request.getInputStream())){

      Gson gson = new Gson();

      return gson.fromJson(reader, Map.class);
    }
    catch(Exception e){
      log.error(e.getMessage());
    }

    return null;
  }

  private void checkAccessToken(String accessToken) throws RefreshTokenException {

    try{
      jwtUtil.validateToken(accessToken);
    }
    catch(ExpiredJwtException e){
      log.info("엑세스 토큰이 만료되었습니다.");
    }
    catch(Exception e){
      throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_ACCESS);
    }
  }

  private Map<String, Object> checkRefreshToken(String refreshToken) throws RefreshTokenException {

    try{
      Map<String, Object> values = jwtUtil.validateToken(refreshToken);
      return values;
    }
    catch(ExpiredJwtException e){
      throw new RefreshTokenException(RefreshTokenException.ErrorCase.OLD_REFRESH);
    }
    catch(MalformedJwtException e){
      log.error("------------------ malformed jwt exception ---------------");
      throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
    }
    catch(Exception e){
      throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
    }

  }

  private void sendTokens(String accessTokenValue, String refreshTokenValue, HttpServletResponse response) {

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    Gson gson = new Gson();

    String jsonStr = gson.toJson(Map.of("access_token", accessTokenValue, "refresh_token", refreshTokenValue));

    try{
      response.getWriter().println(jsonStr);
    }
    catch(IOException e){
      throw new RuntimeException(e);
    }
  }

}
