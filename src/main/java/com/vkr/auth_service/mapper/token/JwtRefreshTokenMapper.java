package com.vkr.auth_service.mapper.token;

import com.vkr.auth_service.entity.token.JwtRefreshToken;
import com.vkr.auth_service.util.steam.SteamUserPrincipal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JwtRefreshTokenMapper {

    /**
     * Преобразование данных о JWT refresh токене в объект для кеширования
     *
     * @param token JWT refresh токен
     * @param user  Данные пользователя
     * @return Объект для кеширования
     */
    @Mapping(source = "user.steamId", target = "steamId")
    @Mapping(source = "expirationTime", target = "expiration")
    JwtRefreshToken toCache(String token, long expirationTime, SteamUserPrincipal user);
}

