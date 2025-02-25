package com.vkr.user_service.controller.auth;

import com.vkr.user_service.dto.user.UserDto;
import com.vkr.user_service.service.user.UserService;
import com.vkr.user_service.util.steam.SteamToken;
import com.vkr.user_service.util.steam.SteamUserPrincipal;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Profile Controller")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping("/profile")
    public UserDto profile() {
        SteamToken authentication = (SteamToken) SecurityContextHolder.getContext().getAuthentication();
        SteamUserPrincipal principal = (SteamUserPrincipal) authentication.getPrincipal();
        return userService.getUserBySteamId(principal.getSteamId());
    }
}
