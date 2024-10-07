package com.example.actionprice.advice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 모든 컨트롤러에서 발생하는 예외를 처리함
 * @author : 연상훈
 * @created : 2024-10-06 오후 6:46
 * @updated : 2024-10-06 오후 11:57
 * @see :
 * RestControllerAdvice는 ControllerAdvice를 기본적으로 상속하기 때문에
 * RestController뿐만 아니라 Controller도 처리 가능.
 * 선언만 하면 spring이 알아서 가져다 사용함.
 */
@RestControllerAdvice
@Log4j2
public class CustomRestAdvice {

  @ExceptionHandler(BindException.class)
  @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
  public ResponseEntity<Map<String, String>> handlerBindException(BindException e) {

    log.error(e);

    Map<String, String> errorMap = new HashMap<>();

    if(e.hasErrors()){
      BindingResult bindingResult = e.getBindingResult();

      bindingResult.getFieldErrors()
          .forEach(fieldError -> {errorMap.put(fieldError.getField(), fieldError.getCode());});
    }

    return ResponseEntity.badRequest().body(errorMap);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
  public ResponseEntity<Map<String, String>> handlerFKException(Exception e) {

    log.error(e);

    Map<String, String> errorMap = new HashMap<>();

    errorMap.put("time", "" + System.currentTimeMillis());
    errorMap.put("message", "constraint fails");

    return ResponseEntity.badRequest().body(errorMap);

  }

  // 존재하지 않는 값을 가져올 때와 삭제할 때
  @ExceptionHandler({NoSuchElementException.class, EmptyResultDataAccessException.class})
  @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
  public ResponseEntity<Map<String, String>> handlerNoSuchElementException(Exception e) {

    log.error(e);

    Map<String, String> errorMap = new HashMap<>();

    errorMap.put("time", "" + System.currentTimeMillis());
    errorMap.put("message", "No Such element Exception");

    return ResponseEntity.badRequest().body(errorMap);
  }


}