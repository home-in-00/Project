package com.example.actionprice.security;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * url path만 관리하는 곳
 * @author 연상훈
 * @created 2024-11-13 오전 12:11
 * @info url path가 너무 많아서 여기서 따로 관리함
 */
@NoArgsConstructor
@Getter
public class UrlPathManager {

  private final String[] PATH_ADMIN = {
      "/api/admin/**", // 어드민페이지
      "/api/post/*/comment/admin/*" // 어드민 코멘트
  };

  private final String[] PATH_AUTHENTICATED = {
      "/api/user/logout", // 로그아웃 요청
      "/api/post/create",
      "/api/post/*/update",
      "/api/post/*/delete",
      "/api/post/*/update/*", // 게시글 생성, 수정, 삭제
      "/api/mypage/**", // 마이페이지(개인정보 열람, 내 게시글 목록, 내 즐겨찾기 목록, 사용자 삭제)
      "/api/category/favorite/**", // 즐겨찾기 삭제
      "/api/category/*/*/*/*/favorite", // 즐겨찾기 생성
      "/api/token/refresh"
  };

  private final String[] PATH_ANONYMOUSE = {
      "/api/user/login" // 로그인 요청
  };

  private final String[] PATH_PERMIT_ALL = {
      "/swagger-ui/**", // 스웨거
      "/v3/api-docs/**", // 스웨거
      "/", // 홈
      "/api/user/**", // 사용자 관련 기능들
      "/api/post/list",
      "/api/post/list*",// 게시글 목록 열람 가능
      "/api/post/*/detail",
      "/api/post/*/detail*", // 게시글 내용 열람 가능
      "/api/post/comments",
      "/api/post/comments*", // 게시글 내 댓글 목록 열람 가능
      "/api/category/**" // 카테고리
  };

}
