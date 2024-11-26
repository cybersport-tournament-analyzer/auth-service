package com.vkr.user_service.entity.token;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "blacklisted-token")
public class JwtBlacklistedToken {

    @Id
    private String token;
}
