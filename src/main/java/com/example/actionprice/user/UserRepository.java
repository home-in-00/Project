package com.example.actionprice.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : 연상훈
 * @created : 2024-10-06 오후 1:14
 * @updated : 2024-10-06 오후 1:14
 */
public interface UserRepository extends JpaRepository<User, String> {
  Optional<User> findById(String username); // id = username
}
