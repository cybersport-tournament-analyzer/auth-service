package com.vkr.user_service.service.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    @Override
    public Map<String, Object> getUserData(String steamUserId) throws Exception {
        String url = String.format("%s/ISteamUser/GetPlayerSummaries/v2/?key=%s&format=json&steamids=%s",
                steamApiUrl, steamApiToken, steamUserId);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("Failed to fetch user data from Steam");
        }

        JsonNode playerData = new ObjectMapper().readTree(response.getBody())
                .path("response")
                .path("players")
                .get(0);
        if (playerData == null) {
            throw new IllegalStateException("No player data found");
        }

        return new ObjectMapper().convertValue(playerData, Map.class);
    }

    @Override
    public String extractSteamId(String claimedId) {
        // Извлечение Steam ID из claimed_id
        String[] parts = claimedId.split("/");
        return parts[parts.length - 1];
    }

}
