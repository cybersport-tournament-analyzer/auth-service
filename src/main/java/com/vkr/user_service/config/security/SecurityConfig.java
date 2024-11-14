package com.vkr.user_service.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /**
     * Список разрешенных URL
     */
    public static final String[] PERMITTED_URL = {
            "/users/**", "/swagger-ui/**", "/swagger-resources/*",
            "/v3/api-docs/**", "/actuator/**"
    };



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(request -> request
                        .requestMatchers(PERMITTED_URL).permitAll()
                        .anyRequest().authenticated());

        return http.build();
    }
}
