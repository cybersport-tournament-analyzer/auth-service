package com.vkr.user_service.service.jwt;

import com.vkr.user_service.property.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractJwtGenerator implements JwtGenerator {

    protected final JwtProperties jwtProperties;

    /**
     * Генерация токена
     *
     * @param extraClaims дополнительные данные
     * @param userDetails данные пользователя
     * @return токен
     */
    protected String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {

        JwtProperties.Secret secret = getSecret();


        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + secret.getExpiration()))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Получение и дешифрование секрета токена
     *
     * @return ключ JWT токена
     */
    private SecretKey getSigningKey() {

        JwtProperties.Secret secret = getSecret();

        byte[] keyBytes = Decoders.BASE64.decode(secret.getKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}