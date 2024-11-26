package com.vkr.user_service.entity.token;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

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
}
