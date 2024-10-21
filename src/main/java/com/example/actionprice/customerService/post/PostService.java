package com.example.actionprice.customerService.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    String createPost(String username,PostForm form);
    String updatePost(Integer postId,PostForm form);
    String deletePost(Integer postId);


    Page<Post> getPostList(int page, String keyword);
//    Page<Post> findPosts(String keyword, Pageable pageable);




}
