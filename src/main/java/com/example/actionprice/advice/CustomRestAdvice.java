package com.example.actionprice.advice;

import com.example.actionprice.exception.*;

import com.example.actionprice.exception.AccessTokenException.TOKEN_ERROR;
import com.example.actionprice.exception.RefreshTokenException.ErrorCase;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.naming.AuthenticationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 모든 컨트롤러에서 발생하는 예외를 처리함
 * @author : 연상훈
 * @created : 2024-10-06 오후 6:46
 * @updated 2024-11-11 오전 5:14 : 토큰 관련 에러 등록, 금지된 요청에 대한 에러 등록
 * @info RestControllerAdvice는 ControllerAdvice를 기본적으로 상속하기 때문에 RestController뿐만 아니라 Controller도 처리 가능. 선언만 하면 spring이 알아서 가져다 사용함.
 */
@RestControllerAdvice
@Log4j2
public class CustomRestAdvice {

  // commons
  // 400 Bad Request - 잘못된 요청 예외 처리
  @ExceptionHandler({ IllegalArgumentException.class, MethodArgumentTypeMismatchException.class })
  public ResponseEntity<String> handleBadRequest(Exception ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("잘못된 요청입니다: " + ex.getMessage());
  }

  // 404 Not Found - 경로를 찾을 수 없는 예외 처리
  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<String> handleNotFound(NoHandlerFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body("요청한 경로를 찾을 수 없습니다.");
  }

  // 409 Conflict - 데이터 무결성 예외 처리
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<String> handleConflict(DataIntegrityViolationException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body("데이터 무결성 위반입니다.");
  }

  // 500 Internal Server Error - 일반적인 예외 처리
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleServerError(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
        body("서버 오류가 발생했습니다.");
  }

  // 422 Unprocessable Entity - 유효성 검사 실패 처리
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
        .body("유효성 검증에 실패했습니다.");
  }

  // customs
  /**
   * BindException(컨트롤러에서의 유효성 검사)를 커스텀하는 handler
   * @author : 연상훈
   * @created : 2024-10-12 오전 12:54
   * @updated : 2024-10-12 오전 12:54
   */
  @ExceptionHandler(BindException.class)
  public ResponseEntity<Map<String, String>> handlerBindException(BindException e) {

    log.error(e);

    Map<String, String> errorMap = new HashMap<>();

    if(e.hasErrors()){
      BindingResult bindingResult = e.getBindingResult();

      bindingResult.getFieldErrors()
          .forEach(fieldError -> errorMap.put(fieldError.getField(), fieldError.getCode()));
    }

    return ResponseEntity.unprocessableEntity()
        .body(errorMap);
  }

  // 존재하지 않는 값을 가져올 때와 삭제할 때
  @ExceptionHandler({NoSuchElementException.class, EmptyResultDataAccessException.class})
  public ResponseEntity<Map<String, String>> handlerNoSuchElementException(Exception e) {

    log.error(e);

    Map<String, String> errorMap = new HashMap<>();

    errorMap.put("time", "" + System.currentTimeMillis());
    errorMap.put("message", "No Such element Exception");

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(errorMap);
  }

  // 인증코드 발송에 실패했을 때
  @ExceptionHandler(InvalidEmailAddressException.class)
  public ResponseEntity<String> handlerInvalidEmailAddressException(InvalidEmailAddressException e) {
    log.error(e);
    return ResponseEntity.unprocessableEntity()
        .body(e.getMessage());
  }

  // 부정한 방법으로 존재하지 않는 값을 조회 시 발생하는 에러
  @ExceptionHandler({
      UserNotFoundException.class,
      PostNotFoundException.class,
      CommentNotFoundException.class,
      InvalidCategoryException.class
  })
  public ResponseEntity<String> handlerUserNotFoundException(Exception e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(e.getMessage());
  }

  // 부정 접근 등
  @ExceptionHandler({
      AccessDeniedException.class,
      AuthenticationException.class,
      InsufficientAuthenticationException.class
  })
  public ResponseEntity<String> handlerAuthenticationError(Exception e) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(e.getMessage());
  }

  // 더이상 즐겨찾기를 추가할 수 없을 때
  @ExceptionHandler(TooManyFavoritesException.class)
  public ResponseEntity<String> handlerTooMuchFavoritesError(Exception e) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(e.getMessage());
  }

  // 엑세스 토큰 에러
  @ExceptionHandler(AccessTokenException.class)
  public ResponseEntity<String> handlerAccessTokenException(AccessTokenException e) {
    TOKEN_ERROR token_error = e.getToken_error();
    return ResponseEntity.status(token_error.getStatus())
        .body(token_error.getMessage());
  }

  // 리프레시 토큰 에러
  @ExceptionHandler(RefreshTokenException.class)
  public ResponseEntity<String> handlerRefreshTokenException(RefreshTokenException e) {
    ErrorCase errorCase = e.getErrorCase();
    return ResponseEntity.status(errorCase.getStatus())
        .body(errorCase.getMessage());
  }
}