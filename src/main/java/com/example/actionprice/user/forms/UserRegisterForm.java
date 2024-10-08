package com.example.actionprice.user.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author : 연상훈
 * @created : 2024-10-05 오후 10:57
 * @updated : 2024-10-05 오후 10:57
 * @see : 사용자가 user로 등록할 때 입력한 데이터를 담아올 객체입니다. 실질적으로 이게 dto 역할입니다.
 * 하나의 form이지만, 상황에 따라 검증이 필요한 영역이 다르기 때문에
 * 검증을 위한 별도의 그룹을 지정하여 처리합니다.
 */
@Getter
@Setter
@ToString
public class UserRegisterForm {

  @NotBlank(message = "사용자 이름은 필수입니다.", groups = {SendVerificationCodeGroup.class, CheckVerificationCodeGroup.class})
  @Size(min = 6, max = 20, message = "사용자 이름은 6자 이상 20자 이하여야 합니다.", groups = {SendVerificationCodeGroup.class, CheckVerificationCodeGroup.class})
  private String username;

  @NotBlank(message = "비밀번호는 필수입니다.", groups = {SendVerificationCodeGroup.class, CheckVerificationCodeGroup.class})
  @Size(min = 8, max = 16, message = "비밀번호는 8자 이상 16자 이하여야 합니다.", groups = {SendVerificationCodeGroup.class, CheckVerificationCodeGroup.class})
  private String password;

  @NotBlank(message = "이메일은 필수입니다.", groups = {SendVerificationCodeGroup.class, CheckVerificationCodeGroup.class})
  @Email(message = "유효한 이메일 주소를 입력하세요.", groups = {SendVerificationCodeGroup.class, CheckVerificationCodeGroup.class})
  private String email;

  @NotBlank(message = "인증코드는 필수입니다.", groups = CheckVerificationCodeGroup.class)
  private String verificationCode;

  /**
   * 검증 시 구분을 위한 그룹입니다. 실질적인 기능은 없습니다.
   * @author : 연상훈
   * @created : 2024-10-06 오후 8:47
   * @updated : 2024-10-06 오후 8:47
   */
  public interface SendVerificationCodeGroup {}
  public interface CheckVerificationCodeGroup {}
}