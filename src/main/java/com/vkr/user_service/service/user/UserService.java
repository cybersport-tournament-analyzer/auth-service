package com.vkr.user_service.service.user;

import com.vkr.user_service.dto.user.UserCreateDto;
import com.vkr.user_service.dto.user.UserDto;
import com.vkr.user_service.dto.user.UserUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDto createUser(UserCreateDto userCreateDto);

    UserDto updateUser(String username, UserUpdateDto userUpdateDto);

    Page<UserDto> getAllUsers(Pageable pageable);

    UserDto getUserByUsername(String username);

    void deleteUser(String username);
}
