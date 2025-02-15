package com.vkr.user_service.controller.auth;

import com.vkr.user_service.util.steam.SteamToken;
import com.vkr.user_service.util.steam.SteamUserPrincipal;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Profile Controller")
//@CrossOrigin("http://localhost:4200/")
public class ProfileController {

    //todo = return smth cooler than dat shit
    @GetMapping("/profile")
    public ResponseEntity<Object> profile() {
        SteamToken authentication = (SteamToken) SecurityContextHolder.getContext().getAuthentication();
        SteamUserPrincipal principal = (SteamUserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(principal);
    }
}
