package com.vkr.user_service.exception;

public class AuthException extends RuntimeException {

    /**
     * Конструктор
     *
     * @param message сообщение об ошибке
     */
    public AuthException(String message) {
        super(message);
    }
}