package com.vkr.user_service.service.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Value("${steam.api.token}")
    private String steamApiToken;

    @Value("${steam.api.url:https://api.steampowered.com}")
    private String steamApiUrl;

    @Value("${faceit.api.key}")
    private String faceitApiKey;


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
    public void logout(HttpServletRequest request) {
        request.getSession().invalidate();
    }

}
