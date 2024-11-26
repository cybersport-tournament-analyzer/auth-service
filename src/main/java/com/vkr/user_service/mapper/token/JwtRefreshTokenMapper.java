package com.vkr.user_service.mapper.token;

import com.vkr.user_service.entity.token.JwtRefreshToken;
import com.vkr.user_service.util.steam.SteamUserPrincipal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.core.userdetails.UserDetails;

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
    JwtRefreshToken toCache(String token, SteamUserPrincipal user);
}

