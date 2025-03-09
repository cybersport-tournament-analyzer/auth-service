package com.vkr.auth_service.exception;

public class InvalidJwtException extends RuntimeException {

    /**
     * Конструктор
     *
     * @param message сообщение об ошибке
     */
    public InvalidJwtException(String message) {
        super(message);
    }
}
