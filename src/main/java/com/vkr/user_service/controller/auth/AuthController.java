package com.vkr.user_service.controller.auth;

import com.vkr.user_service.dto.steam.SteamOpenIdLoginDto;
import com.vkr.user_service.service.auth.AuthService;
import com.vkr.user_service.util.SteamToken;
import com.vkr.user_service.util.SteamUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@RestController
@RequiredArgsConstructor
@RequestMapping("/steam")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final AuthService service;

    @GetMapping("/login")
    public ResponseEntity<Void> redirectToSteam() {
        String openIdUrl = "https://steamcommunity.com/openid/login?openid.ns=http://specs.openid.net/auth/2.0"
                + "&openid.mode=checkid_setup"
                + "&openid.return_to=http://localhost:8080/steam/login/redirect"
                + "&openid.realm=http://localhost:8080"
                + "&openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select"
                + "&openid.identity=http://specs.openid.net/auth/2.0/identifier_select";
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(openIdUrl)).build();
    }

    @GetMapping("/login/redirect")
    public ResponseEntity<Object> loginRedirect(HttpServletRequest request, @RequestParam Map<String, String> allRequestParams) {
        try {
            // Создание DTO для OpenID
            SteamOpenIdLoginDto dto = new SteamOpenIdLoginDto(
                    allRequestParams.get("openid.ns"),
                    allRequestParams.get("openid.mode"),
                    allRequestParams.get("openid.op_endpoint"),
                    allRequestParams.get("openid.claimed_id"),
                    allRequestParams.get("openid.identity"),
                    allRequestParams.get("openid.return_to"),
                    allRequestParams.get("openid.response_nonce"),
                    allRequestParams.get("openid.assoc_handle"),
                    allRequestParams.get("openid.signed"),
                    allRequestParams.get("openid.sig")
            );

            String steamUserId = service.extractSteamId(allRequestParams.get("openid.claimed_id"));


            SteamToken authReq = new SteamToken(steamUserId);
            Authentication auth = authenticationManager.authenticate(authReq);

            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(auth);
            request.getSession(true).setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);

            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/steam/profile")).build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during login");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<Object> profile() {
        SteamToken authentication = (SteamToken) SecurityContextHolder.getContext().getAuthentication();
        SteamUserPrincipal principal = (SteamUserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(principal);
    }

    @GetMapping("/failed")
    public ResponseEntity<String> failed() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest request) {
        service.logout(request);
    }
}
