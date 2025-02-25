package com.vkr.user_service.service.jwt;

import com.vkr.user_service.entity.token.JwtRefreshToken;
import com.vkr.user_service.mapper.token.JwtRefreshTokenMapper;
import com.vkr.user_service.property.JwtProperties;
import com.vkr.user_service.repository.token.JwtRefreshTokenRepository;
import com.vkr.user_service.util.steam.SteamUserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtRefreshGenerator extends AbstractJwtGenerator {

    private final JwtRefreshTokenMapper jwtRefreshTokenMapper;
    private final JwtRefreshTokenRepository jwtRefreshTokenRepository;

    public JwtRefreshGenerator(JwtProperties jwtProperties,
                               JwtRefreshTokenMapper jwtRefreshTokenMapper,
                               JwtRefreshTokenRepository jwtRefreshTokenRepository) {
        super(jwtProperties);
        this.jwtRefreshTokenMapper = jwtRefreshTokenMapper;
        this.jwtRefreshTokenRepository = jwtRefreshTokenRepository;
    }

    @Override
    public String generateToken(UserDetails userDetails) {

        if (userDetails instanceof SteamUserPrincipal principal) {
            JwtRefreshToken token = jwtRefreshTokenRepository.findById(principal.getSteamId()).orElse(null);

            if (token == null) {
                String newToken = generateToken(null, userDetails);
                long expirationTime = getSecret().getExpiration() / 1000;
                token = jwtRefreshTokenMapper.toCache(newToken, expirationTime, principal);
                token = jwtRefreshTokenRepository.save(token);
            }

            return token.getToken();
        }
        throw new IllegalArgumentException("Invalid user details type");
    }

    @Override
    public JwtProperties.Secret getSecret() {
        return jwtProperties.getTokens().get("refresh");
    }
}