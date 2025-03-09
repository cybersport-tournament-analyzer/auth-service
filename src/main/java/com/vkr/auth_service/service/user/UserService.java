package com.vkr.auth_service.service.user;

import com.vkr.auth_service.dto.user.UserDto;
import com.vkr.auth_service.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {


    Page<UserDto> getAllUsers(Pageable pageable);

    UserDto getUserByUsername(String username);

    UserDto getUserBySteamId(String steamId);

    void deleteUser(String username);

    UserDetailsService userDetailsService();

    User getBySteamId(String steamId);
}
