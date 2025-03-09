package com.vkr.auth_service.service.blacklist;

public interface BlacklistService {

    /**
     * Добавление токена в blacklist
     *
     * @param token access токен
     */
    void addToBlacklist(String token);

    /**
     * Проверка наличия токена в blacklist
     *
     * @param token access токен
     * @return true - токен в blacklist, false - токен не в blacklist
     */
    boolean isBlacklisted(String token);
}
