package com.example.actionprice.security.jwt.accessToken;

import com.example.actionprice.exception.AccessTokenException;
import com.example.actionprice.exception.AccessTokenException.TOKEN_ERROR;
import com.example.actionprice.security.jwt.JWTUtil;
import com.example.actionprice.security.jwt.refreshToken.RefreshTokenService;
import com.google.gson.Gson;
import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * @value accessTokenValidityInMinutes : 엑세스 토큰 유효 시간 : 60(분)
 * @value jwtUtil
 * @value refreshTokenService
 * @author 연상훈
 * @created 2024-10-20 오후 4:25
 * @info 리프레시 토큰에 관한 것은 모두 refreshTokenService에서 처리하니까 여기서는 엑세스 토큰만 관리
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class AccessTokenServiceImpl implements AccessTokenService {

  private final JWTUtil jwtUtil;
  private final RefreshTokenService refreshTokenService;

  private final int accessTokenValidityInMinutes = 60; // 60분. 분단위

  /**
   * 로그인 성공 후 토큰(엑세스 토큰 + 리프레시 토큰) 발급
   * @param username
   * @author 연상훈
   * @created 2024-10-20 오후 4:09
   * @info 사용자 검사, 리프레시 토큰 검사 및 발급은 refreshTokenService에서 다 처리함
   * @info 그러니 여기서는 엑세스 토큰 발급만 하면 됨
   */
  @Override
  public String issueJwt(String username) {
    Map<String, Object> claim = Map.of("username", username);

    // 리프레시 토큰 관련 검증 및 객체 관리를 처리함.
    // 자원을 아끼기 위해 클레임은 깨알 같이 돌려 씀
    refreshTokenService.issueRefreshToken(username, claim);

    // issueRefreshToken에서 문제 없었으니 엑세스 토큰 발급
    String accessToken = jwtUtil.generateToken(claim, accessTokenValidityInMinutes);
    return returnWithJson(accessToken, username);
  }

  /**
   * 리프레시 토큰 필터에서 엑세스 토큰만 발급함
   * @param username
   * @author 연상훈
   * @created 2024-10-20 오후 4:09
   * @info 사용자 검사, 리프레시 토큰 검사는 refreshTokenService에서 다 처리함
   * @info 그러니 여기서는 엑세스 토큰 발급만 하면 됨
   */
  @Override
  public String issueAccessToken(String username) {
    // 리프레시 토큰 관련 검증 및 객체 관리를 처리함.
    refreshTokenService.checkRefreshFirst(username);

    // checkRefreshFirst에서 문제 없었으니 엑세스 토큰 발급
    String accessToken = jwtUtil.generateToken(Map.of("username", username), accessTokenValidityInMinutes);

    return returnWithJson(accessToken, username);
  }

  /**
   * 리퀘스트 안에서 access token의 값을 읽어서 검사 진행
   * @author 연상훈
   * @created 2024-10-20 오후 4:24
   */
  @Override
  public Map<String, Object> validateAccessTokenInReqeust(HttpServletRequest request) {
    String headerStr = request.getHeader("Authorization");
    log.info("headerStr : " + headerStr);

    if(headerStr == null || headerStr.length() < 8){
      throw new AccessTokenException(TOKEN_ERROR.UNACCEPT);
    }

    String tokenType = headerStr.substring(0,6);
    String tokenStr = headerStr.substring(7);
    log.info("tokenType : " + tokenType);
    log.info("tokenStr : " + tokenStr);


    if(tokenType.equalsIgnoreCase("Bearer") == false){
      // 엑세스 토큰에 Bearer가 빠져 있음. 형식이 이상함
      throw new AccessTokenException(TOKEN_ERROR.BADTYPE);
    }

    log.info("this is bearer : " + tokenType);

    // 엑세스 토큰에 대한 엄격한 검사 후 결과 반환
    return checkAccessToken(tokenStr);
  }

  /**
   * 엑세스 토큰에 대한 모든 검사 진행
   * @author 연상훈
   * @created 2024-10-20 오후 4:24
   */
  @Override
  public Map<String,Object> checkAccessToken(String accessToken) {
    try {
      Map<String,Object> values = jwtUtil.validateToken(accessToken);
      log.info("values : " + values);

      return values;
    } catch (ExpiredJwtException e) {
      log.error("만료된 엑세스 토큰입니다.");
      throw new AccessTokenException(TOKEN_ERROR.EXPIRED);
    } catch (MalformedJwtException e) {
      log.error("잘못된 형식입니다. 위변조 가능성이 있는 엑세스 토큰입니다.");
      throw new AccessTokenException(TOKEN_ERROR.MALFORM);
    } catch (UnsupportedJwtException e) {
      log.error("잘못된 형식입니다. 위변조 가능성이 있는 엑세스 토큰입니다.");
      throw new AccessTokenException(TOKEN_ERROR.MALFORM);
    } catch (ClaimJwtException e) {
      log.error("잘못된 클레임입니다. 위변조 가능성이 있는 엑세스 토큰입니다.");
      throw new AccessTokenException(TOKEN_ERROR.UNEXPECTED);
    } catch (SignatureException e) {
      log.error("잘못된 서명입니다. 위변조 가능성이 있는 엑세스 토큰입니다.");
      throw new AccessTokenException(TOKEN_ERROR.BADSIGN);
    } catch (JwtException e){
      log.error("엑세스 토큰 검사 중 기타 오류가 발생하였습니다.");
      throw new AccessTokenException(TOKEN_ERROR.MALFORM);
    }
  }

  /**
   * 토큰 발급 후 결과를 json 형태로 반환
   * @param accessToken
   * @param username
   * @author 연상훈
   * @created 2024-10-20 오후 1:27
   * @info 리프레시 토큰은 내부적으로만 관리하고, 반환하는 건 엑세스 토큰만
   */
  private String returnWithJson(String accessToken, String username){
    return new Gson().toJson(Map.of("access_token", accessToken, "username", username));
  }
}