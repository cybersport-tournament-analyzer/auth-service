package com.vkr.auth_service.service.auth;

import com.vkr.auth_service.dto.response.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface AuthService {


    public Map<String, Object> getUserData(String steamUserId) throws Exception;

    public String extractSteamId(String claimedId);

    /**
     * Аутентификация пользователя и создание access/refresh токенов
     *
     * @param response         ответ для добавления refresh токена в cookie
     * @param allRequestParams
     * @return новый access токен
     */
    ResponseDto login(HttpServletResponse response, Map<String, String> allRequestParams);

    /**
     * Выход пользователя из сети и добавление access токена в blacklist
     *
     * @param request запрос с access токеном в header
     */
    void logout(HttpServletRequest request);

    /**
     * Создание нового access токена при помощи refresh токена
     *
     * @param request запрос с refresh токеном в cookie
     * @return новый access токен
     */
    ResponseDto refresh(HttpServletRequest request);

}
