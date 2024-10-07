package com.example.actionprice.sendEmail;

/**
 * @author : 연상훈
 * @created : 2024-10-06 오후 9:17
 * @updated : 2024-10-06 오후 9:17
 */
public interface SendEmailService {
  String sendVerificationEmail(String email);
  String checkVerificationCode(String email, String verificationCode);
}
