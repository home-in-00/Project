package com.example.actionprice.sendEmail;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 
 * @author : 연상훈
 * @created : 2024-10-06 오후 7:48
 * @updated : 2024-10-06 오후 7:48
 * @see : 
 */
public interface VerificationEmailRepository extends JpaRepository<VerificationEmail, String> {
  Optional<VerificationEmail> findById(String email); // id = email
}
