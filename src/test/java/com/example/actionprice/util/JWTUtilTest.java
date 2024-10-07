package com.example.actionprice.util;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class JWTUtilTest {

  @Autowired
  private JWTUtil jwtUtil;

  @Test
  public void issueToken() {
    Map<String, Object> claimMap = Map.of("username", "useruser");

    String jwtStr = jwtUtil.generateToken(claimMap, 1);

    assertNotNull(jwtStr);  // 생성된 JWT 문자열이 null이 아님을 확인
    System.out.println("Generated JWT: " + jwtStr);
  }

  @Test
  @Disabled
  public void validateToken() {
    Map<String, Object> claimMap = Map.of("username", "useruser");
    String jwtStr = jwtUtil.generateToken(claimMap, 1);

    Map<String, Object> claims = jwtUtil.validateToken(jwtStr);

    assertEquals("useruser", claims.get("username"));  // JWT에서 얻은 사용자 이름이 일치하는지 확인
  }

  @Test
  @Disabled
  public void getUsernameFromToken() {
    Map<String, Object> claimMap = Map.of("username", "useruser");
    String jwtStr = jwtUtil.generateToken(claimMap, 1);

    String username = jwtUtil.getUsernameFromToken(jwtStr);

    assertEquals("useruser", username);  // JWT에서 얻은 사용자 이름이 일치하는지 확인
  }

  @Test
  @Disabled
  public void validateInvalidToken() {
    String invalidToken = "invalid.token.here";

    assertThrows(JwtException.class, () -> {
      jwtUtil.validateToken(invalidToken);  // 잘못된 토큰이 예외를 발생시켜야 함
    });
  }
}
