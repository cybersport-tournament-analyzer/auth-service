package com.vkr.user_service.controller;

import com.vkr.user_service.dto.user.UserCreateDto;
import com.vkr.user_service.dto.user.UserDto;
import com.vkr.user_service.dto.user.UserUpdateDto;
import com.vkr.user_service.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody UserCreateDto userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @PatchMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@PathVariable String username, @RequestBody UserUpdateDto userDto) {
        return userService.updateUser(username, userDto);
    }

    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
    }
}
