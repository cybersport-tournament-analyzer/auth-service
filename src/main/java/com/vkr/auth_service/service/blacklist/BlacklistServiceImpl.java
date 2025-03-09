package com.vkr.auth_service.service.blacklist;

import com.vkr.auth_service.entity.token.JwtBlacklistedToken;
import com.vkr.auth_service.repository.token.JwtBlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlacklistServiceImpl implements BlacklistService {

    private final JwtBlacklistedTokenRepository jwtBlacklistedTokenRepository;

    @Override
    public void addToBlacklist(String token) {
        jwtBlacklistedTokenRepository.save(new JwtBlacklistedToken(token));
    }

    @Override
    public boolean isBlacklisted(String token) {
        return jwtBlacklistedTokenRepository.existsById(token);
    }
}

