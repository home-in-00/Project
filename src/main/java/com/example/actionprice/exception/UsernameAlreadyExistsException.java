package com.example.actionprice.exception;

/**
 * 중복된 유저네임 입력으로 회원가입이 실패했을 때의 예외처리
 * @author 연상훈
 * @created 2024-10-10 오전 11:08
 * @updated 2024-10-10 오전 11:08
 */
public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
