package com.vkr.auth_service.entity.token;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "refresh-token")
public class JwtRefreshToken {

    @Id
    private String steamId;
    private String token;
    @TimeToLive
    private Long expiration;

}
