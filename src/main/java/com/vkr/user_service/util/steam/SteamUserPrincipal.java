package com.vkr.user_service.util.steam;

import com.vkr.user_service.entity.user.User;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class SteamUserPrincipal implements UserDetails {

    private final User user;
    private Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;

    public static SteamUserPrincipal create(User user, Map<String, Object> attributes) {
        List<GrantedAuthority> authorities = Collections.
                singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        return new SteamUserPrincipal(user, Collections.unmodifiableMap(attributes), authorities);
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
