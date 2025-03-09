package com.vkr.auth_service.util.steam;

import lombok.*;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
public class SteamToken extends AbstractAuthenticationToken {

    private final SteamUserPrincipal principal;
    private final String             steamId;

    public SteamToken(String steamId) {
        super(null);
        this.steamId = steamId;
        this.principal = null;
        this.setAuthenticated(false);
    }

    public SteamToken(String steamId, SteamUserPrincipal principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.steamId = steamId;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
