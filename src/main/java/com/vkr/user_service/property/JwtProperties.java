package com.vkr.user_service.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.Map;

@Data
@RefreshScope
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Секреты для генерации токенов
     */
    private Map<String, Secret> tokens;

    /**
     * Секрет для генерации токенов
     */
    @Data
    public static class Secret {

        /**
         * Ключ для генерации токенов
         */
        private String key;

        /**
         * Время жизни токена в миллисекундах
         */
        private long expiration;
    }
}