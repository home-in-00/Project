package com.example.actionprice.exception;

import org.springframework.http.HttpStatus;

/**
 * 엑세스 토큰 에러
 * @author : 연상훈
 * @created : 2024-10-06 오후 2:57
 * @updated 2024-10-17 오후 7:42 : 상태코드 숫자 수정
 * @updated 2024-10-19 오후 5:17 : 블랙리스트 기능 구현을 위해 TOKEN_ERROR에 BLOCKED 추가
 * @updated 2024-11-11 오전 5:16 : 불필요한 응답 방식 제거
 */
public class AccessTokenException extends RuntimeException {

  private final TOKEN_ERROR token_error;

  public enum TOKEN_ERROR {
    UNACCEPT(HttpStatus.UNAUTHORIZED, "Token is null or too short"),
    BADTYPE(HttpStatus.UNAUTHORIZED, "Token type must be Bearer"),
    EXPIRED(HttpStatus.I_AM_A_TEAPOT, "Expired Token"),
    MALFORM(HttpStatus.FORBIDDEN, "Malformed Token"),
    UNEXPECTED(HttpStatus.FORBIDDEN, "Unexpected claim"),
    BADSIGN(HttpStatus.FORBIDDEN, "Bad Signature Token");

    private HttpStatus status;
    private String message;

    TOKEN_ERROR(HttpStatus status, String message) {
      this.status = status;
      this.message = message;
    }

    public HttpStatus getStatus() {
      return status;
    }

    public String getMessage() {
      return message;
    }
  }

  public AccessTokenException(TOKEN_ERROR error) {
    super(error.name());
    this.token_error = error;
  }

  public TOKEN_ERROR getToken_error() {
    return token_error;
  }

}
