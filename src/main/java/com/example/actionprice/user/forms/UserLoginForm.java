package com.example.actionprice.user.forms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author : 연상훈
 * @created : 2024-10-05 오후 10:57
 * @updated : 2024-10-05 오후 10:57
 * @see : 사용자가 로그인할 때 입력한 데이터를 담아올 객체입니다. 실질적으로 이게 dto 역할입니다.
 */
@Data
@AllArgsConstructor
public class UserLoginForm {

  @NotBlank(message = "사용자 이름은 필수 입력 사항입니다.")
  @Size(min = 6, max=20, message = "사용자 이름은 6자 이상 20자 이하로 입력해야 합니다.")
  private String username;

  @NotBlank(message = "사용자 이름은 필수 입력 사항입니다.")
  @Size(min = 8, max=16, message = "비밀번호는 8자 이상 16자 이하로 입력해야 합니다.")
  private String password;

  private boolean rememberMe = false; // rememberMe(자동 로그인)을 위한 항목

}
