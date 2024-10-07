package com.example.actionprice.user;

import com.example.actionprice.customerService.comment.Comment;
import com.example.actionprice.user.favorite.Favorite;
import com.example.actionprice.customerService.post.Post;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * @author : 연상훈
 * @created : 2024-10-05 오후 10:04
 * @updated : 2024-10-06 오후 12:29
 * @see :
 * 1. 순환참조의 문제를 피하기 위해 @ToString(exclude={})와 @JsonManagedReference를 사용했습니다.
 * 2. 원활한 하위 객체 관리를 위해 orphanRemoval = true과 cascade = {CascadeType.ALL}를 사용했습니다.
 * 3. 객체 호출 시의 효율을 위해 fetch = FetchType.LAZY와 @BatchSize(size)를 사용했습니다.
 * 4. 이메일은 회원가입 시 필수이기 때문에 unique이며, not null입니다.
 * 5. 즐겨찾기가 과도하게 늘어나는 것을 막기 위해 10개로 제한했습니다.
 * 6. OneToMany 관계에 있는 객체의 컬렉션 타입은, 해당 객체를 쉽게 찾아낼 수 있도록 set으로 설정했습니다.
 * 7. jwt 토큰은 로그인 시에 발급하는 것이지, 계정 생성 시에 발급하는 것이 아니기 때문에 생성할 당시에는 발급되지 않습니다. 그렇기 때문에 nullable입니다.
 * 8. 유연한 권한 관리를 위해 role을 Set<GrantedAuthority> authorities로 변경했습니다. 추후 "ROLE_USER", "ROLE_ADMIN"과 같은 입력이 필요합니다.
 * 9.추후 수정 및 추가가 예고된 항목들의 수정 및 추가를 위해 Builder를 사용하는 것은 불필요한 코드의 증가를 야기하므로, set 메서드를 지정하여 단순화합니다.
 */
@Entity
@Table(name="user")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"postSet", "commentSet", "favoriteSet"})
public class User {

  // field

  // field - basic
  @Id
  @Column(name = "username")
  @Size(min = 6, max=20)
  private String username;

  @Column(nullable=false, name = "password")
  private String password;

  @Column(nullable=false, unique=true)
  @Email
  private String email;

  @Column(nullable=true)
  private String refreshToken;

  @ElementCollection(fetch = FetchType.EAGER)
  @Builder.Default
  private Set<GrantedAuthority> authorities = new HashSet<>();

  // field - relationship
  @JsonManagedReference //부모객체에서 자식객체 관리 json형태로 반환될때 이게 부모라는것을 알려줌
  @OneToMany(mappedBy = "user",
      orphanRemoval = true,
      cascade = {CascadeType.ALL},
      fetch = FetchType.LAZY)
  @BatchSize(size = 10)
  @Builder.Default
  private Set<Post> postSet = new HashSet<>();

  @JsonManagedReference
  @OneToMany(mappedBy = "user",
      orphanRemoval = true,
      cascade = {CascadeType.ALL},
      fetch = FetchType.LAZY)
  @BatchSize(size = 10)
  @Builder.Default
  private Set<Comment> commentSet = new HashSet<>();

  @JsonManagedReference
  @OneToMany(mappedBy = "user",
      orphanRemoval = true,
      cascade = {CascadeType.ALL},
      fetch = FetchType.LAZY)
  @BatchSize(size = 10)
  @Size(max = 10)
  @Builder.Default
  private Set<Favorite> favoriteSet = new HashSet<>();

  // method
  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public void setPassword(@Size(min = 8, max = 16) String password) {
    this.password = password;
  }

  /**
   * @author : 연상훈
   * @created : 2024-10-06 오전 11:09
   * @updated : 2024-10-06 오전 11:09
   * @param : role = 해당 user에게 추가할 role입니다. 반드시 "ROLE_USER", "ROLE_ADMIN"로 입력해주세요.
   */
  public void addAuthorities(String role) {
    GrantedAuthority authority = new SimpleGrantedAuthority(role);
    this.authorities.add(authority);
  }

  /**
   * @author : 연상훈
   * @created : 2024-10-06 오전 11:11
   * @updated : 2024-10-06 오전 11:11
   * @param : role = 해당 user에게 삭제할 role입니다. 반드시 "ROLE_USER", "ROLE_ADMIN"로 입력해주세요.
   */
  public void removeAuthorities(String role) {
    GrantedAuthority authority = new SimpleGrantedAuthority(role);
    this.authorities.remove(authority);
  }

  public void addPost(Post post) {
    this.postSet.add(post);
  }

  public void removePost(Post post) {
    this.postSet.remove(post); // post는 서로 1부모-1자식이기 때문에 단순한 로직이면 충분함.
  }

  public void addComment(Comment comment) {
    this.commentSet.add(comment);
  }

  /**
   * @author : 연상훈
   * @created : 2024-10-06 오후 12:17
   * @updated : 2024-10-06 오후 12:17
   * @see : comment는 user와 post를 모두 부모로 가지기 때문에 제거할 때 더 조심해야 함. comment 객체의 삭제 과정 설명
   * 1. 부모 객체에서 지움
   * 2. orphanRemoval = true, cascade = {CascadeType.ALL}로, 부모와의 연결 끊어지면 지워짐
   * 3. 근데 지워진다는 신호를 받으면 지워지기 전에 @EntityListeners(CommentListener.class) 내부의 comment 객체 제거 로직이 발동
   * 4. 양쪽 부모에서 안전하게 지워짐.
   */
  public void removeComment(Comment comment) {
    if (commentSet.contains(comment)) {
      commentSet.remove(comment);
      comment.setUser(null);
    }
  }

  public void addFavorite(Favorite favorite) {
    this.favoriteSet.add(favorite);
  }

  public void removeFavorite(Favorite favorite) {
    this.favoriteSet.remove(favorite);
  }
}
