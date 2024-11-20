package com.vkr.user_service.controller.auth;

import com.vkr.user_service.dto.steam.SteamOpenIdLoginDto;
import com.vkr.user_service.service.auth.AuthService;
import com.vkr.user_service.util.SteamToken;
import com.vkr.user_service.util.SteamUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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

    @GetMapping("login")
    public ResponseEntity<Object> login() {
        return ResponseEntity.ok(service.getOpenIdAttributes());
    }

    @GetMapping("login/redirect")
    public ResponseEntity<Object> loginRedirect(HttpServletRequest request, @RequestParam Map<String, String> allRequestParams) {
        SteamOpenIdLoginDto dto = new SteamOpenIdLoginDto(
                allRequestParams.get("openid.ns"),
                allRequestParams.get("openid.op_endpoint"),
                allRequestParams.get("openid.claimed_id"),
                allRequestParams.get("openid.identity"),
                allRequestParams.get("openid.return_to"),
                allRequestParams.get("openid.response_nonce"),
                allRequestParams.get("openid.assoc_handle"),
                allRequestParams.get("openid.signed"),
                allRequestParams.get("openid.sig")
        );

        try {
//            String steamUserId = service.validateLoginParameters(dto);
            //TODO: СДЕЛАТЬ ВАЛИДАЦИЮ ИЛИ БЕЗ НЕЕ ХЫЗЫ + ВЫЦЕПИТЬ ID ТОК ЦИФРУУУ
            String steamUserId = dto.getClaimed_id();
            SteamToken authReq = new SteamToken(steamUserId);
            Authentication auth = authenticationManager.authenticate(authReq);
            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(auth);
            HttpSession session = request.getSession(true);
            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Login failed");
        }

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/steam/profile")).build();
    }

    @GetMapping("profile")
    public ResponseEntity<Object> success() {
        SteamToken authentication = (SteamToken) SecurityContextHolder.getContext().getAuthentication();
        SteamUserPrincipal principal = (SteamUserPrincipal) authentication.getPrincipal();

        return ResponseEntity.ok(principal);
    }

    @GetMapping("failed")
    public ResponseEntity<Object> failed() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
    }
}
