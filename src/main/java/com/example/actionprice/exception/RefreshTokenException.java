package com.example.actionprice.exception;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * @author : 연상훈
 * @created : 2024-10-06 오후 2:58
 * @updated : 2024-10-06 오후 2:58
 */
public class RefreshTokenException extends RuntimeException {

  private ErrorCase errorCase;

  public enum ErrorCase {
    NO_ACCESS, BAD_ACCESS, NO_REFRESH, OLD_REFRESH, BAD_REFRESH
  }

  public RefreshTokenException(ErrorCase errorCase) {
    super(errorCase.name());
    this.errorCase = errorCase;
  }

  public void sendResponseError(HttpServletResponse response){

    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    Gson gson = new Gson();

    String responseStr = gson.toJson(Map.of("msg", errorCase.name(), "time", new Date()));

    try{
      response.getWriter().print(responseStr);
    }
    catch(IOException e){
      throw new RuntimeException(e);
    }
  }

}
