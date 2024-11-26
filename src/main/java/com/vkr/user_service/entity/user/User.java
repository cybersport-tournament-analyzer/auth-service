package com.vkr.user_service.entity.user;

import com.vkr.user_service.entity.avatar.UserProfilePic;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "steam_id", length = 64, unique = true, nullable = false)
    private String steamId;

    @Column(name = "username", length = 64, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 64, nullable = false)
    private String password;

    @Column(name = "hours_played", nullable = true)
    private Long hoursPlayed;

    @Column(name = "rating_elo", nullable = true)
    private Long ratingElo;

    @Column(name = "faceit_winrate", nullable = true)
    private Long faceitWinrate;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
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
