package com.example.actionprice.user.favorite;

import com.example.actionprice.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author : 연상훈
 * @created : 2024-10-05 오후 10:45
 * @updated : 2024-10-06 오후 12:29
 * @see :
 * 1. @JsonBackReference와 @ToString(exclude = {"user"})로 순환참조의 위험을 경감시켰습니다.
 * 2. url은 단순하게 구현해서 String타입으로만 저장하도록 합시다.
 * 3. size(min=1)을 사용해서 아무것도 입력하지 않은 url은 즐겨찾기로 등록될 수 없도록 하였습니다.
 * 4. 이걸 참조하는 쪽에서 OneToMany로 불러올 때 순번대로 정렬하기 위해 Comparable을 implements 함
 */
@Entity
@Table(name = "favorite")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Favorite implements Comparable<Favorite> {

  // field

  // field - basic
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "favoriteId")
  private int favoriteId;

  @Column(nullable = false)
  @Size(min = 1)
  private String favoriteURL;

  // field - relationship
  @JsonBackReference
  @ManyToOne
  @JoinColumn(name = "username", nullable = false)
  private User user;

  // method
  @Override
  public int compareTo(Favorite o) {
    return this.favoriteId - o.favoriteId;
  }
}
