package com.example.actionprice.exception;

/**
 * 중복된 유저네임 입력으로 회원가입이 실패했을 때의 예외처리
 * 근데 구글 stmp는 잘못된 주소로 전송됐을 때의 오류를 알려주지 않아서 의미가 없어짐
 * @author 연상훈
 * @created 2024-10-10 오전 11:08
 * @updated 2024-10-10 오전 11:08
 */
public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
