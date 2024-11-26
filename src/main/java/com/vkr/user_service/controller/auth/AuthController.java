package com.vkr.user_service.controller.auth;

import com.vkr.user_service.dto.response.ResponseDto;
import com.vkr.user_service.dto.steam.SteamOpenIdLoginDto;
import com.vkr.user_service.service.auth.AuthService;
import com.vkr.user_service.service.jwt.JwtAccessGenerator;
import com.vkr.user_service.util.steam.SteamToken;
import com.vkr.user_service.util.steam.SteamUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@CrossOrigin("*")
@Tag(name = "Auth Controller")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService service;

    @GetMapping("/login")
    public ResponseEntity<Void> redirectToSteam() {
        String openIdUrl = "https://steamcommunity.com/openid/login?openid.ns=http://specs.openid.net/auth/2.0"
                + "&openid.mode=checkid_setup"
                + "&openid.return_to=http://localhost:8080/auth/login/redirect"
                + "&openid.realm=http://localhost:8080"
                + "&openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select"
                + "&openid.identity=http://specs.openid.net/auth/2.0/identifier_select";
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(openIdUrl)).build();
    }

    @GetMapping("/login/redirect")
    public ResponseDto loginRedirect(HttpServletResponse response, @RequestParam Map<String, String> allRequestParams) {

        return service.login(response, allRequestParams);
    }

    @PostMapping("/refresh")
    public ResponseDto refresh(HttpServletRequest request) {
        return service.refresh(request);
    }


    @GetMapping("/failed")
    public ResponseEntity<String> failed() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        service.logout(request);
    }
}
