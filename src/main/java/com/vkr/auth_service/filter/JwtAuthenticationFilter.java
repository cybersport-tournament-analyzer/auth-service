package com.vkr.auth_service.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vkr.auth_service.client.UserServiceClient;
import com.vkr.auth_service.dto.user.CreateUserDto;
import com.vkr.auth_service.dto.user.Role;
import com.vkr.auth_service.dto.user.UserDto;
import com.vkr.auth_service.handler.ErrorResponse;
import com.vkr.auth_service.service.auth.AuthService;
import com.vkr.auth_service.service.jwt.JwtGenerator;
import com.vkr.auth_service.util.jwt.JwtUtil;
import com.vkr.auth_service.util.steam.SteamAuthenticationProvider;
import com.vkr.auth_service.util.steam.SteamToken;
import com.vkr.auth_service.util.steam.SteamUserPrincipal;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static com.vkr.auth_service.config.security.SecurityConfig.PERMITTED_URL;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtGenerator jwtAccessGenerator;
    private final AuthService steamService;
    private final ObjectMapper objectMapper;
    private final SteamAuthenticationProvider steamAuthenticationProvider;
    private final UserServiceClient userServiceClient;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException {
        try {
            String token = jwtUtil.extractTokenFromRequestHeader(request);
            Claims claims = jwtUtil.extractAllClaims(token, jwtAccessGenerator);

            if (!jwtUtil.isTokenBlacklisted(token)) {
                authorize(claims, request);
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            onError(request, response, e);
        }
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return Arrays.stream(PERMITTED_URL)
                .anyMatch(e -> new AntPathMatcher().match(e, request.getServletPath()));
    }

    private void authorize(Claims claims, HttpServletRequest request) {

        String steamId = claims.getSubject();
        System.out.println(steamId);
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Map<String, Object> userAttributes;
        try {
            userAttributes = steamService.getUserData(steamId);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String username = (String) userAttributes.get("personaname");
        Integer faceitElo = !userAttributes.get("faceit_elo").equals("N/A") ? (Integer) userAttributes.get("faceit_elo") : 0;
        String avatar = (String) userAttributes.get("avatarfull");
        String steamLink = (String) userAttributes.get("profileurl");

        CreateUserDto userDto = CreateUserDto.builder()
                .steamId(steamId)
                .steamUsername(username)
                .ratingElo(Long.valueOf(faceitElo))
                .avatarImageLink(avatar)
                .steamProfileLink(steamLink)
                .role(Role.USER)
                .build();

        UserDto user = userServiceClient.saveUser(userDto);
        SteamUserPrincipal steamUserPrincipal = SteamUserPrincipal.create(user, userAttributes);

        SteamToken authToken = new SteamToken(steamId, steamUserPrincipal, steamUserPrincipal.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        context.setAuthentication(steamAuthenticationProvider.authenticate(authToken));
        SecurityContextHolder.setContext(context);
    }

    /**
     * Обработка ошибки аутентификации
     *
     * @param request запрос
     * @param response ответ
     * @param e        исключение
     *
     * @throws IOException если возникнет ошибка при записи в поток
     */
    private void onError(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {

        log.error("Ошибка аутентификации: {}", e.getLocalizedMessage());

        ErrorResponse errorResponse = new ErrorResponse(e, request.getRequestURI());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
