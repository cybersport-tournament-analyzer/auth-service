package com.vkr.user_service.util;

import com.vkr.user_service.entity.user.User;
import com.vkr.user_service.repository.user.UserRepository;
import com.vkr.user_service.service.auth.AuthService;
import com.vkr.user_service.service.user.UserService;
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
        User user = userOptional.orElseGet(() -> {
            String username = (String) userAttributes.get("personaname");
            return userRepository.save(new User(UUID.randomUUID(), steamId, username, 0L, 0L, 0L, LocalDateTime.now()));
        });
        SteamUserPrincipal steamUserPrincipal = SteamUserPrincipal.create(user, userAttributes);

        return new SteamToken(steamId, steamUserPrincipal, steamUserPrincipal.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(SteamToken.class);
    }
}
