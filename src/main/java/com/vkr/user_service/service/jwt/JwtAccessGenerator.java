package com.vkr.user_service.service.jwt;

import com.vkr.user_service.property.JwtProperties;
import com.vkr.user_service.util.steam.SteamUserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JwtAccessGenerator extends AbstractJwtGenerator {

    public JwtAccessGenerator(JwtProperties jwtProperties) {
        super(jwtProperties);
    }

    @Override
    public String generateToken(UserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();

        if (userDetails instanceof SteamUserPrincipal principal) {
            claims.put("id", principal.getId());
            claims.put("steamId", principal.getSteamId());
            claims.put("role", principal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
        }

        return generateToken(claims, userDetails);
    }

    @Override
    public JwtProperties.Secret getSecret() {
        return jwtProperties.getTokens().get("access");
    }
}
