package com.example.actionprice.sendEmail;


import com.example.actionprice.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author : 연상훈
 * @created : 2024-10-06 오후 7:15
 * @updated : 2024-10-06 오후 7:15
 * @see : 
 */
@Data
@Builder
@Entity
@Table(name = "verification_email")
@AllArgsConstructor
@NoArgsConstructor
public class VerificationEmail extends BaseEntity {

  @Id
  private String email;

  @Column(nullable=false, length = 8)
  private String verificationCode;
}
