package com.vkr.auth_service.util.steam;

import com.vkr.auth_service.client.UserServiceClient;
import com.vkr.auth_service.dto.user.CreateUserDto;
import com.vkr.auth_service.dto.user.Role;
import com.vkr.auth_service.dto.user.UserDto;
import com.vkr.auth_service.service.auth.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class SteamAuthenticationProvider implements AuthenticationProvider {

    private final UserServiceClient userServiceClient;

    private final AuthService steamService;


    @Override
    public Authentication authenticate(Authentication authentication) {
        String steamId = ((SteamToken) authentication).getSteamId();

        Map<String, Object> userAttributes;
        try {
            userAttributes = steamService.getUserData(steamId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        String username = (String) userAttributes.get("personaname");
        Integer faceitElo = !userAttributes.get("faceit_elo").equals("N/A") ? (Integer) userAttributes.get("faceit_elo") : 0;
        String avatar = (String) userAttributes.get("avatarfull");
        String steamLink = (String) userAttributes.get("profileurl");

        CreateUserDto userDto = CreateUserDto.builder()
                .steamId(steamId)
                .steamUsername(username)
                .ratingElo(Long.valueOf(faceitElo))
                .avatarImageLink(avatar)
                .steamProfileLink(steamLink)
                .role(Role.USER)
                .build();

        UserDto user = userServiceClient.saveUser(userDto);

        SteamUserPrincipal steamUserPrincipal = SteamUserPrincipal.create(user, userAttributes);

        return new SteamToken(steamId, steamUserPrincipal, steamUserPrincipal.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(SteamToken.class);
    }
}
