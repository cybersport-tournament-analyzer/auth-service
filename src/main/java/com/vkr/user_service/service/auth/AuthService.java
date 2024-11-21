package com.vkr.user_service.service.auth;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface AuthService {


    public Map<String, Object> getUserData(String steamUserId) throws Exception;

    public String extractSteamId(String claimedId);

    public void logout(HttpServletRequest request);

}
