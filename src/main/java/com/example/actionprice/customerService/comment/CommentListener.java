package com.example.actionprice.customerService.comment;

import com.example.actionprice.customerService.post.Post;
import com.example.actionprice.user.User;
import jakarta.persistence.PreRemove;

/**
 * @author : 연상훈
 * @created : 2024-10-06 오후 12:02
 * @updated : 2024-10-06 오후 12:02
 * @see : comment가 user와 post를 부모로 가지기 때문에 한 쪽에서 삭제 시 문제가 발생할 수 있습니다. 그 문제를 삭제 이벤트를 전파하는 방법으로 해결합니다.
 */
public class CommentListener {

  @PreRemove
  public void preRemove(Comment comment) {

    User user = comment.getUser();
    if (user != null) {
      user.getCommentSet().remove(comment);
    }

    Post post = comment.getPost();
    if (post != null) {
      post.getCommentSet().remove(comment);
    }
  }
}
