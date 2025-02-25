package com.vkr.user_service.entity.token;

import com.vkr.user_service.property.JwtProperties;
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
