package com.example.actionprice.customerService.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;

    @PostMapping(value = "/create" , consumes = MediaType.APPLICATION_JSON_VALUE)
    public Integer createPost(@Validated(PostForm.PostCreateGroup.class) @RequestBody PostForm form){
        log.info("[class] PostController - [method] createPost - username : {}", form.getUsername());
        return postService.createPost(form).getPostId();
    }

    @GetMapping(value = "/{id}/detail")
    public PostDetailDTO goDetailPost(@PathVariable("id") Integer postId) {
        log.info("goDetailPost");
        return postService.getDetailPost(postId);
    }

    @GetMapping(value = "/{id}/update")
    public PostDetailDTO goUpdatePost(@PathVariable("id") Integer postId) {
        log.info("goUpdatePost");
        return postService.getDetailPost(postId);
    }

    @PostMapping(value = "/{id}/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public PostDetailDTO updatePost(@PathVariable("id") Integer id, @RequestBody @Validated(PostForm.PostUpdateGroup.class) PostForm form) {
        return postService.updatePost(id, form);
    }

    @PostMapping(value = "/{id}/delete" ,consumes = MediaType.APPLICATION_JSON_VALUE)
    public String deletePost(@PathVariable("id") Integer id, @RequestBody Map<String, String> usernameMap) {
        String logined_username = usernameMap.get("logined_username");
        log.info("[class] PostController - [method] deletePost - id : {} | username : {}", id, logined_username);
        postService.deletePost(id, logined_username);
        return "delete successful";
    }

    @GetMapping("/list")
    public List<PostDetailDTO> getPostList(@RequestParam(defaultValue = "0") int page, @RequestParam(required = false) String keyword) {
        log.info("[class] PostController - [method] getPostList - page : {} | keyword : {}", page, keyword);
        List<PostDetailDTO> postList = postService.getPostList(page, keyword);
        return postList;
    }

}
