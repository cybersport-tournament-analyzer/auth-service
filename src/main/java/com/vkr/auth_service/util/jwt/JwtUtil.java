package com.vkr.auth_service.util.jwt;

import com.vkr.auth_service.exception.AuthException;
import com.vkr.auth_service.exception.InvalidJwtException;
import com.vkr.auth_service.property.JwtProperties;
import com.vkr.auth_service.service.blacklist.BlacklistService;
import com.vkr.auth_service.service.jwt.JwtGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    private final BlacklistService blacklistService;

    /**
     * Проверка токена на наличие в черном списке
     *
     * @param token токен
     * @return true, если токен в черном списке
     */
    public boolean isTokenBlacklisted(String token) {
        return blacklistService.isBlacklisted(token);
    }

    /**
     * Извлечение всех данных из токена
     *
     * @param token токен
     * @param generator генератор токена
     * @return данные из токена
     *
     * @throws InvalidJwtException если токен невалиден
     */
    public Claims extractAllClaims(String token, JwtGenerator generator) throws InvalidJwtException {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey(generator))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new InvalidJwtException(e.getLocalizedMessage());
        }
    }

    /**
     * Извлечение токена из куки запроса
     *
     * @param request запрос
     * @return токен
     *
     * @throws AuthException если токен не найден
     */
    public String extractTokenFromRequestCookie(HttpServletRequest request) throws AuthException {

        String token = null;

        if (request.getCookies() != null) {
            for (Cookie cookie: request.getCookies()) {
                if (cookie.getName().equals("refresh-token")) {
                    token = cookie.getValue();
                }
            }
        }

        if (token == null) {
            throw new AuthException("No cookie refresh token");
        }

        return token;
    }

    /**
     * Извлечение токена из заголовка запроса
     *
     * @param request запрос
     * @return токен
     *
     * @throws AuthException если токен не найден
     */
    public String extractTokenFromRequestHeader(HttpServletRequest request) throws AuthException {

        String authHeader = request.getHeader(HEADER_NAME);
        if (StringUtils.hasText(authHeader) && StringUtils.startsWithIgnoreCase(authHeader, BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }

        throw new AuthException("No header access token");
    }

    /**
     * Добавление refresh токена в куки ответа
     *
     * @param response ответ
     * @param token токен
     * @param generator генератор токена
     */
    public void addResponseCookie(HttpServletResponse response, String token, JwtGenerator generator) {

        long expiration = generator.getSecret().getExpiration();

        ResponseCookie cookie = ResponseCookie.from("refresh-token", token)
                .httpOnly(false)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofMillis(expiration))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * Получение ключа для подписи токена
     *
     * @param generator генератор токена
     * @return ключ
     */
    private SecretKey getSigningKey(JwtGenerator generator) {

        JwtProperties.Secret secret = generator.getSecret();

        byte[] keyBytes = Decoders.BASE64.decode(secret.getKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

