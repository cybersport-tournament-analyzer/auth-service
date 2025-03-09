package com.vkr.auth_service.config.redis;

import com.vkr.auth_service.entity.token.JwtBlacklistedToken;
import com.vkr.auth_service.entity.token.JwtRefreshToken;
import com.vkr.auth_service.property.RedisCacheProperty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RedisCacheConfig {

    private final RedisCacheProperty redisCacheProperty;

    /**
     * Настройка кэш-менеджера
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.of(redisCacheProperty.getDefaultTtl(), ChronoUnit.SECONDS));
        return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration);
    }

    /**
     * Конфигурация ключевого пространства
     */
    @SuppressWarnings("unused")
    public class RedisKeyspaceConfiguration extends KeyspaceConfiguration {

        /**
         * Список сущностей для кэширования
         */
        private static final List<Class<?>> entityClasses =  List.of(
                JwtRefreshToken.class,
                JwtBlacklistedToken.class
        );

        /**
         * Начальная конфигурация
         */
        @Override
        protected @NonNull Iterable<KeyspaceSettings> initialConfiguration() {

            return entityClasses.stream().map(this::getKeyspaceSettings).toList();
        }

        /**
         * Получение настроек ключевого пространства для класса
         */
        @Override
        public @NonNull KeyspaceSettings getKeyspaceSettings(@NonNull Class<?> type) {

            String cacheName = type.getAnnotation(RedisHash.class).value();

            KeyspaceSettings keyspaceSettings = new KeyspaceSettings(type, cacheName);
            keyspaceSettings.setTimeToLive(redisCacheProperty.getCacheSettings().get(cacheName).getTtl());

            return keyspaceSettings;
        }
    }
}
