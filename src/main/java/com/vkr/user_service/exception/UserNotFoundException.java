package com.vkr.user_service.exception;

public class UserNotFoundException extends RuntimeException {

    /**
     * Конструктор
     *
     * @param field поле, по которому искался пользователь
     * @param value значение поля, по которому искался пользователь
     */
    public UserNotFoundException(String field, String value) {
        super("User with " + field + "=" + value + " not found");
    }
}
