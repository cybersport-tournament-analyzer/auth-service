package com.vkr.user_service.service.auth;

import com.vkr.user_service.dto.steam.SteamOpenIdLoginDto;

import java.util.Map;

public interface AuthService {

    public Map<String, Object> getUserData(String steamUserId) throws Exception;

    public String validateLoginParameters(SteamOpenIdLoginDto dto) throws IllegalArgumentException;

    public Map<String, String> getOpenIdAttributes();

}
