package com.example.actionprice.customerService.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Integer>{
    
    Page<Post> findByTitleContainingOrUser_UsernameContaining(String titleKeyword, String usernameKeyword, Pageable pageable);

    Page<Post> findByUser_UsernameAndTitleContaining(String username, String keyword, Pageable pageable);
    Page<Post> findByUser_Username(String username, Pageable pageable);
}
