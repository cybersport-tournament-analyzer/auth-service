package com.vkr.auth_service.controller;

import com.vkr.auth_service.client.UserServiceClient;
import com.vkr.auth_service.dto.user.UserDto;
import com.vkr.auth_service.util.steam.SteamToken;
import com.vkr.auth_service.util.steam.SteamUserPrincipal;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Profile Controller")
@RequiredArgsConstructor
public class ProfileController {

    private final UserServiceClient userService;

    @GetMapping("/profile")
    public UserDto profile() {
        SteamToken authentication = (SteamToken) SecurityContextHolder.getContext().getAuthentication();
        SteamUserPrincipal principal = (SteamUserPrincipal) authentication.getPrincipal();
        return userService.getUserBySteamId(principal.getSteamId());
    }
}
