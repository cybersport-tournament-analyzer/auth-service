package com.vkr.user_service.service.jwt;

import com.vkr.user_service.property.JwtProperties;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtGenerator {

    /**
     * Генерация нового токена
     *
     * @param userDetails данные пользователя
     * @return токен
     */
    String generateToken(UserDetails userDetails);

    /**
     * Получение секрета для токена
     *
     * @return секрет JWT токена
     */
    JwtProperties.Secret getSecret();
}

