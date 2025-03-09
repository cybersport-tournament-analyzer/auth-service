package com.vkr.auth_service.dto.user;

import com.vkr.auth_service.entity.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class UserDto {

    private String steamId;
    private String steamUsername;
    private Long ratingElo;
    private String avatarImageLink;
    private String steamProfileLink;
    private Role role;
}
