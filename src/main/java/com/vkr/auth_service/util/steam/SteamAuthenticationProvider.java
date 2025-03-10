package com.vkr.auth_service.util.steam;

import com.vkr.auth_service.entity.user.Role;
import com.vkr.auth_service.entity.user.User;
import com.vkr.auth_service.repository.user.UserRepository;
import com.vkr.auth_service.service.auth.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class SteamAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;

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
        Optional<User> userOptional = userRepository.findBySteamId(steamId);
        User user;
        String username = (String) userAttributes.get("personaname");
        Integer faceitElo = !userAttributes.get("faceit_elo").equals("N/A") ? (Integer) userAttributes.get("faceit_elo") : 0;
        String avatar = (String) userAttributes.get("avatarfull");
        String steamLink = (String) userAttributes.get("profileurl");
        if (userOptional.isEmpty()) {
            user = userRepository.save(new User(UUID.randomUUID(), steamId, username, Long.valueOf(faceitElo), avatar, steamLink, LocalDateTime.now(), Role.USER));
        } else {
            user = userOptional.get();
            user.setAvatarImageLink(userOptional.get().getAvatarImageLink());
            user.setSteamProfileLink(userOptional.get().getSteamProfileLink());
            user.setRatingElo(Long.valueOf(faceitElo));
            user.setSteamUsername(username);
            user = userRepository.save(user);
        }

        SteamUserPrincipal steamUserPrincipal = SteamUserPrincipal.create(user, userAttributes);

        return new SteamToken(steamId, steamUserPrincipal, steamUserPrincipal.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(SteamToken.class);
    }
}
