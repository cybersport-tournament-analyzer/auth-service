package com.vkr.auth_service.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vkr.auth_service.dto.response.ResponseDto;
import com.vkr.auth_service.service.auth.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth Controller")
public class AuthController {

    private final AuthService service;

    //        String openIdUrl = "https://steamcommunity.com/openid/login?openid.ns=http://specs.openid.net/auth/2.0"
//                + "&openid.mode=checkid_setup"
//                + "&openid.return_to=http://109.172.95.212:8080/auth/login/redirect"
//                + "&openid.realm=http://109.172.95.212:8080"
//                + "&openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select"
//                + "&openid.identity=http://specs.openid.net/auth/2.0/identifier_select";
    @JsonProperty("openIdUrl")
    String openIdUrl = "https://steamcommunity.com/openid/login?openid.ns=http://specs.openid.net/auth/2.0"
            + "&openid.mode=checkid_setup"
            + "&openid.return_to=http://109.172.95.212:8086/auth/login/redirect"
            + "&openid.realm=http://109.172.95.212:8086"
            + "&openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select"
            + "&openid.identity=http://specs.openid.net/auth/2.0/identifier_select";

    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> redirectToSteam() {
        Map<String, String> response = new HashMap<>();
        response.put("openIdUrl", openIdUrl);
        return ResponseEntity.ok(response);
        //        String openIdUrl = "https://steamcommunity.com/openid/login?openid.ns=http://specs.openid.net/auth/2.0"
        //                + "&openid.mode=checkid_setup"
        //                + "&openid.return_to=http://localhost:8080/auth/login/redirect"
        //                + "&openid.realm=http://localhost:8080"
        //                + "&openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select"
        //                + "&openid.identity=http://specs.openid.net/auth/2.0/identifier_select";
        //        return ResponseEntity.ok(new HashMap<String, String>() {'openIdUrl':openIdUrl});
        //        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(openIdUrl)).build();

    }


    @GetMapping("/login/redirect")
    public void loginRedirect(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, String> allRequestParams) throws IOException {
        ResponseDto loginResponse = service.login(response, allRequestParams);

        String requestUrl = request.getRequestURL().toString();
        String baseUrl = requestUrl.replace(request.getRequestURI(), "");

        String redirectUrl;
        if (baseUrl.contains("localhost")) {
            redirectUrl = "http://localhost:4200/callback-token?accessToken=" + loginResponse.getAccessToken();
        } else {
            redirectUrl = "http://109.172.95.212:4200/callback-token?accessToken=" + loginResponse.getAccessToken();
        }

        response.sendRedirect(redirectUrl);
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
