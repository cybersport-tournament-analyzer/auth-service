package com.vkr.user_service.repository.token;

import com.vkr.user_service.entity.token.JwtBlacklistedToken;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtBlacklistedTokenRepository extends KeyValueRepository<JwtBlacklistedToken, String> {
}
