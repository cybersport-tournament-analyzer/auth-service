package com.vkr.user_service.service.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vkr.user_service.dto.response.ResponseDto;
import com.vkr.user_service.entity.token.JwtRefreshToken;
import com.vkr.user_service.exception.AuthException;
import com.vkr.user_service.exception.InvalidJwtException;
import com.vkr.user_service.repository.token.JwtRefreshTokenRepository;
import com.vkr.user_service.service.blacklist.BlacklistService;
import com.vkr.user_service.service.jwt.JwtGenerator;
import com.vkr.user_service.service.user.UserService;
import com.vkr.user_service.util.jwt.JwtUtil;
import com.vkr.user_service.util.steam.SteamToken;
import com.vkr.user_service.util.steam.SteamUserPrincipal;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final JwtGenerator jwtAccessGenerator;
    private final JwtGenerator jwtRefreshGenerator;
    private final AuthenticationManager authenticationManager;
    private final JwtRefreshTokenRepository jwtRefreshTokenRepository;
    private final BlacklistService blacklistService;

    @Value("${steam.api.token}")
    private String steamApiToken;

    @Value("${steam.api.url}")
    private String steamApiUrl;

    @Value("${faceit.api.key}")
    private String faceitApiKey;

    public AuthServiceImpl(JwtUtil jwtUtil, UserService userService,
                           JwtGenerator jwtAccessGenerator, JwtGenerator jwtRefreshGenerator,
                           @Lazy AuthenticationManager authenticationManager,
                           JwtRefreshTokenRepository jwtRefreshTokenRepository,
                           BlacklistService blacklistService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.jwtAccessGenerator = jwtAccessGenerator;
        this.jwtRefreshGenerator = jwtRefreshGenerator;
        this.authenticationManager = authenticationManager;
        this.jwtRefreshTokenRepository = jwtRefreshTokenRepository;
        this.blacklistService = blacklistService;
    }

    @Override
    public Map<String, Object> getUserData(String steamUserId) throws Exception {
        String steamUrl = String.format("%s/ISteamUser/GetPlayerSummaries/v2/?key=%s&format=json&steamids=%s",
                steamApiUrl, steamApiToken, steamUserId);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> steamResponse = restTemplate.getForEntity(steamUrl, String.class);
        if (steamResponse.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("Failed to fetch user data from Steam");
        }

        JsonNode playerData = new ObjectMapper().readTree(steamResponse.getBody())
                .path("response")
                .path("players")
                .get(0);
        if (playerData == null) {
            throw new IllegalStateException("No player data found");
        }

        Map<String, Object> userData = new ObjectMapper().convertValue(playerData, Map.class);

        String faceitUrl = "https://open.faceit.com/data/v4/players?game=cs2&game_player_id=" + steamUserId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + faceitApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> faceitEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> faceitResponse = restTemplate.exchange(faceitUrl, HttpMethod.GET, faceitEntity, String.class);

            if (faceitResponse.getStatusCode() == HttpStatus.OK) {
                JsonNode faceitData = new ObjectMapper().readTree(faceitResponse.getBody())
                        .path("games")
                        .path("cs2");

                if (!faceitData.isMissingNode()) {
                    int faceitElo = faceitData.path("faceit_elo").asInt();
                    userData.put("faceit_elo", faceitElo);
                } else {
                    userData.put("faceit_elo", "N/A");
                }
            } else {
                userData.put("faceit_elo", "N/A");
            }
        } catch (Exception e) {
            userData.put("faceit_elo", "N/A");
            System.err.println("Failed to fetch data from Faceit API: " + e.getMessage());
        }

        return userData;
    }


    @Override
    public String extractSteamId(String claimedId) {
        String[] parts = claimedId.split("/");
        return parts[parts.length - 1];
    }

    @Override
    public ResponseDto login(HttpServletResponse response, Map<String, String> allRequestParams) {


//            SteamOpenIdLoginDto dto = new SteamOpenIdLoginDto(
//                    allRequestParams.get("openid.ns"),
//                    allRequestParams.get("openid.mode"),
//                    allRequestParams.get("openid.op_endpoint"),
//                    allRequestParams.get("openid.claimed_id"),
//                    allRequestParams.get("openid.identity"),
//                    allRequestParams.get("openid.return_to"),
//                    allRequestParams.get("openid.response_nonce"),
//                    allRequestParams.get("openid.assoc_handle"),
//                    allRequestParams.get("openid.signed"),
//                    allRequestParams.get("openid.sig")
//            );

        String steamUserId = extractSteamId(allRequestParams.get("openid.claimed_id"));


        SteamToken authReq = new SteamToken(steamUserId);
        Authentication auth = authenticationManager.authenticate(authReq);

//            SecurityContext sc = SecurityContextHolder.getContext();
//            sc.setAuthentication(auth);
//            request.getSession(true).setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);

        SteamUserPrincipal principal = (SteamUserPrincipal) auth.getPrincipal();
        String accessToken = jwtAccessGenerator.generateToken(principal);
        String refreshToken = jwtRefreshGenerator.generateToken(principal);


        jwtUtil.addResponseCookie(response, refreshToken, jwtRefreshGenerator);

        return new ResponseDto(accessToken);
    }


    @Override
    public void logout(HttpServletRequest request) {

        String token = jwtUtil.extractTokenFromRequestHeader(request);

        blacklistService.addToBlacklist(token);
    }

    @Override
    public ResponseDto refresh(HttpServletRequest request) {

        try {
            String token = jwtUtil.extractTokenFromRequestCookie(request);
            Claims claims = jwtUtil.extractAllClaims(token, jwtRefreshGenerator);

            String username = claims.getSubject();
            String cacheToken = getCachedRefreshToken(username);


            if (token.equals(cacheToken)) {

                UserDetails user = userService
                        .userDetailsService()
                        .loadUserByUsername(username);

                String accessToken = jwtAccessGenerator.generateToken(user);

                return new ResponseDto(accessToken);
            } else {
                throw new InvalidJwtException("Invalid JWT refresh token: " + token);
            }
        } catch (Exception e) {
            throw new AuthException("Token not valid: " + e.getMessage());
        }
    }

    /**
     * Получение refresh токена из кэша
     *
     * @param username имя пользователя
     * @return refresh токен
     */
    private String getCachedRefreshToken(String username) {

        JwtRefreshToken tokenCache = jwtRefreshTokenRepository.findById(username)
                .orElseThrow(() -> new AuthException("No such token in cache: " + username));

        return tokenCache.getToken();
    }
}
