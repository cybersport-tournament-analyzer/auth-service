package com.vkr.user_service.service.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vkr.user_service.dto.steam.SteamOpenIdLoginDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final DataSourceTransactionManagerAutoConfiguration dataSourceTransactionManagerAutoConfiguration;
    private Map<String, String> openIdAttributes;
//    @Value("${steam.token}")
    private final String steamApiToken = "ECD36B5DF3DFA8065A21C3B09F63881B";
    private final String steamApiUrl = "https://api.steampowered.com";
    @Value("${server.port}")
    private String serverPort;

    public Map<String, Object> getUserData(String steamUserId) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("%s/ISteamUser/GetPlayerSummaries/v2/?key=%s&format=json&steamids=%s", steamApiUrl, steamApiToken, steamUserId);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (!response.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
            throw new Exception();
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree = mapper.readTree(response.getBody());
        Iterator<JsonNode> playersIterator = tree.get("response").get("players").iterator();
        Map<String, Object> map = (Map<String, Object>) mapper.convertValue(playersIterator.next(), Map.class);

        return map;
    }

    public String validateLoginParameters(SteamOpenIdLoginDto dto) throws IllegalArgumentException {
        MultiValueMap<String, String> openidRequest = getStringStringMultiValueMap(dto);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(openidRequest, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.postForEntity("https://steamcommunity.com/openid/login", request, String.class);
        System.out.println(response.getStatusCode());
        if (!response.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
            System.out.println("попался тут");
            throw new IllegalArgumentException();
        }

        Map<String, String> responseBody = this.parseLoginValidationResponse(response.getBody());

        if (!responseBody.containsKey("ns") || !responseBody.get("ns").equals("http://specs.openid.net/auth/2.0")) {
            System.out.println("попался тут 2");
            throw new IllegalArgumentException();
        }

        if (!responseBody.containsKey("is_valid") || !responseBody.get("is_valid").equals("true")) {
            System.out.println("попался тут 3");
            throw new IllegalArgumentException();
        }

        Pattern p = Pattern.compile("^https?://steamcommunity.com/openid/id/(7656119\\d{10})/?$");
        Matcher m = p.matcher(dto.getIdentity());

        if (!m.find()) {
            System.out.println("попался тут 4");
            throw new IllegalArgumentException();
        }

        return m.group(1);
    }

    private static MultiValueMap<String, String> getStringStringMultiValueMap(SteamOpenIdLoginDto dto) {
        MultiValueMap<String, String> openidRequest = new LinkedMultiValueMap<>();
        openidRequest.add("openid_ns", dto.getNs());
        openidRequest.add("openid_op_endpoint", dto.getOp_endpoint());
        openidRequest.add("openid_claimed_id", dto.getClaimed_id());
        openidRequest.add("openid_identity", dto.getIdentity());
        openidRequest.add("openid_return_to", dto.getReturn_to());
        openidRequest.add("openid_response_nonce", dto.getResponse_nonce());
        openidRequest.add("openid_assoc_handle", dto.getAssoc_handle());
        openidRequest.add("openid_signed", dto.getSigned());
        openidRequest.add("openid_sig", dto.getSig());
        openidRequest.add("openid.mode", "check_authentication");
        return openidRequest;
    }

    private Map<String, String> parseLoginValidationResponse(String response) {
        if (response == null) return Map.of();

        Map<String, String> body = new HashMap<>();
        for (String line : response.split("\n")) {
            if (line.isEmpty()) continue;
            String[] values = line.split(":", 2);
            String key = values[0];
            String value = values[1];

            body.put(key, value);
        }

        return body;
    }

    public Map<String, String> getOpenIdAttributes() {
        if (this.openIdAttributes == null) {
            Map<String, String> openIdAttributes = new HashMap<>();
            openIdAttributes.put("openid_realm", String.format("http://localhost:%s/", serverPort));
            openIdAttributes.put("openid_return_to", String.format("http://localhost:%s/steam/login/redirect", serverPort));
            this.openIdAttributes = Collections.unmodifiableMap(openIdAttributes);
        }

        return openIdAttributes;
    }
}
