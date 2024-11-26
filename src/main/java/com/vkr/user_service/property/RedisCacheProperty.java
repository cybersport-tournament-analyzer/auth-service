package com.vkr.user_service.property;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.Map;

@Data
@RefreshScope
@ConfigurationProperties(prefix = "spring.data.redis.cache")
public class RedisCacheProperty {

    /**
     * Время жизни кэша по умолчанию
     */
    private Long defaultTtl;

    /**
     * Настройки кэша
     */
    private Map<String, CacheSettings> cacheSettings;

    /**
     * Настройки кэша
     */
    @Data
    public static class CacheSettings {

        /**
         * Имя кэша
         */
        private String name;

        /**
         * Время жизни кэша в миллисекундах
         */
        private Long ttl;
    }

    /**
     * Инициализация настроек (изменение null на defaultTtl)
     */
    @PostConstruct
    public void init() {
        cacheSettings.forEach((key, value) -> {
            if (value.getTtl() == null) {
                value.setTtl(defaultTtl);
            }
        });
    }
}
