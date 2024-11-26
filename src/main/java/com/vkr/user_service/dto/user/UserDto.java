package com.vkr.user_service.dto.user;

import com.vkr.user_service.entity.user.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String steamId;

    private String username;

    private Long hoursPlayed;

    private Long ratingElo;

    private Long faceitWinrate;

    private LocalDateTime createdAt;

    private Role role;
}
