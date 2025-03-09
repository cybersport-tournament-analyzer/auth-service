package com.vkr.auth_service.repository.token;

import com.vkr.auth_service.entity.token.JwtRefreshToken;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtRefreshTokenRepository extends KeyValueRepository<JwtRefreshToken, String> {
}

