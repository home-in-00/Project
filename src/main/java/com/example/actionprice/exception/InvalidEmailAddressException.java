package com.example.actionprice.exception;

/**
 * 잘못된 이메일 입력으로 이메일 발송이 실패했을 때의 예외처리
 * @author 연상훈
 * @created 2024-10-10 오전 11:08
 * @updated 2024-10-10 오전 11:08
 */
public class InvalidEmailAddressException extends RuntimeException {
    public InvalidEmailAddressException(String message) {
        super(message);
    }
}
