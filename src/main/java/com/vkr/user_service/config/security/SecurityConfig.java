package com.vkr.user_service.config.security;

import com.vkr.user_service.filter.JwtAuthenticationFilter;
import com.vkr.user_service.util.steam.SteamAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SteamAuthenticationProvider steamAuthenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    /**
     * Список разрешенных URL
     */
    public static final String[] PERMITTED_URL = {
            "/users/**", "/swagger-ui/**", "/auth/**", "/profile/**", "/swagger-resources/*",
            "/v3/api-docs/**", "/actuator/**"
    };



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(request -> request
                        .requestMatchers(PERMITTED_URL).permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(steamAuthenticationProvider)
                .exceptionHandling(handler -> handler.authenticationEntryPoint((request, response, e) -> response.sendError(401, e.getMessage())))
                .addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class);

        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(steamAuthenticationProvider);

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
