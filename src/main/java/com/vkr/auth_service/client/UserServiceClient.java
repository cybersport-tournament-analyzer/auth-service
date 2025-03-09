package com.vkr.auth_service.client;

import com.vkr.auth_service.dto.user.CreateUserDto;
import com.vkr.auth_service.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", url = "http://109.172.95.212:8080")
public interface UserServiceClient {

    @GetMapping("/users/steam/{steamId}")
    UserDto getUserBySteamId(@PathVariable String steamId);

    @PostMapping("/users")
    UserDto saveUser(@RequestBody CreateUserDto createUserDto);

}