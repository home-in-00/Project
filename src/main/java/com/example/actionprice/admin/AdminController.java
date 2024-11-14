package com.example.actionprice.admin;

import com.example.actionprice.security.jwt.refreshToken.RefreshTokenService;
import com.example.actionprice.user.UserService;
import java.util.Map;

import com.example.actionprice.user.dto.UserListDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 어드민 페이지 컨트롤러
 * @author 연상훈
 * @created 2024-11-07 오후 11:15
 * @value userService
 * @value refreshTokenService
 */
@RestController
@RequestMapping("/api/admin")
@Log4j2
@RequiredArgsConstructor
public class AdminController {

  private final UserService userService;
  private final RefreshTokenService refreshTokenService;

  /**
   * 어드민 페이지의 사용자 리스트를 반환하는 메서드
   * @param pageNum 기본값 = 0
   * @author 연상훈
   * @created 2024-11-07 오후 11:15
   * @info 어드민 전용 페이지에서 사용되는 메서드이기 때문에 어드민만 사용 가능
   * @info pageNum과 key는 있으면 있는대로, 없으면 없는대로 알아서 처리함
   * @info username 또는 email에 keyword가 포함된 user를 검색함
   */
  @Secured("ROLE_ADMIN")
  @GetMapping("/userlist")
  public UserListDTO getUserList(
      @RequestParam(name = "pageNum", defaultValue = "0", required = false) Integer pageNum,
      @RequestParam(name = "keyword", defaultValue = "", required = false) String keyword
  ) {
    log.info("[class] AdminController - [method] getUserList - page : {} | keyword : {}", pageNum, keyword);
    return userService.getUserList(keyword, pageNum);
  }

  /**
   * 어드민 페이지에서 사용자를 블록 처리하는 메서드
   * @param selected_username 선택한 사용자 이름
   * @author 연상훈
   * @created 2024-11-07 오후 11:15
   * @info 어드민 전용 페이지에서 사용되는 메서드이기 때문에 어드민만 사용 가능
   * @info 해당 사용자의 리프레시 토큰을 block 처리함
   * @info 리프레시 토큰을 통한 제한이기 때문에 회원가입만 하고 로그인 한 적 없는 사용자는 차단 불가
   * @info 굳이 차단하고 싶으면 사용자에게 토큰을 발급해 준 다음 차단할 수도 있음
   */
  @Secured("ROLE_ADMIN")
  @PostMapping("/userlist/{username}/block")
  public Map<String, Object> setBlockUser(@PathVariable("username") String selected_username) {

    boolean result = refreshTokenService.setBlockUserByUsername(selected_username);
    String message = result ? "blocked" : "unblocked";
    return Map.of("message", message, "isBlocked", result);
  }

  /**
   * 어드민 페이지의 사용자에게 리프레시 토큰을 새로 발급해주는 메서드
   * @param selected_username 선택한 사용자 이름
   * @author 연상훈
   * @created 2024-11-07 오후 11:15
   * @info 어드민 전용 페이지에서 사용되는 메서드이기 때문에 어드민만 사용 가능
   * @info 리프레시 토큰을 탈취 당한 상황을 가정했기 때문에 기존의 토큰을 삭제한 후 새로 발급함
   */
  @Secured("ROLE_ADMIN")
  @PostMapping("/userlist/{username}/reset")
  public Map<String, String> resetUser(@PathVariable("username") String selected_username) {
    log.info("delete target : {}", selected_username);
    refreshTokenService.resetRefreshToken(selected_username);
    String message = String.format("user(%s)'s refresh token was reset", selected_username);
    return Map.of(
        "selected_username", selected_username,
        "message", message
    );
  }

}
