package com.vkr.auth_service.dto.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String roleName;

    @Override
    public String toString() { return roleName; }

    public static Role fromString(String roleName) {
        for(Role role: Role.values()) {
            if(role.roleName.equals(roleName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No such role: " + roleName);
    }
}
