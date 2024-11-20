package com.vkr.user_service.service.user;

import com.vkr.user_service.dto.user.UserDto;
import com.vkr.user_service.dto.user.UserUpdateDto;
import com.vkr.user_service.entity.user.User;
import com.vkr.user_service.mapper.user.UserMapper;
import com.vkr.user_service.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    @Override
    public UserDto getUserByUsername(String username) {
        User user = findUserByUsername(username);

        return userMapper.toDto(user);
    }

    @Override
    public UserDto getUserBySteamId(String steamId) {
        return userRepository.findBySteamId(steamId).map(userMapper::toDto).orElse(null);
    }

    @Override
    public void deleteUser(String username) {
        User user = findUserByUsername(username);

        userRepository.delete(user);

        log.info("User deleted: {}", user);
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
