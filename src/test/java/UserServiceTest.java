import com.vkr.user_service.dto.user.UserDto;
import com.vkr.user_service.entity.user.User;
import com.vkr.user_service.exception.UserNotFoundException;
import com.vkr.user_service.mapper.user.UserMapper;
import com.vkr.user_service.repository.user.UserRepository;
import com.vkr.user_service.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        User user = new User();
        user.setUsername("testuser");
        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toDto(user)).thenReturn(new UserDto().builder().username("testuser").build());

        Page<UserDto> result = userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("testuser", result.getContent().get(0).getUsername());

        verify(userRepository, times(1)).findAll(pageable);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    void testGetUserByUsername_UserExists() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(new UserDto().builder().username("testuser").build());

        UserDto result = userService.getUserByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());

        verify(userRepository, times(1)).findByUsername(username);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    void testGetUserByUsername_UserNotFound() {
        String username = "nonexistent";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserByUsername(username));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testDeleteUser_UserExists() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteUser(username);

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        String username = "nonexistent";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(username));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testGetUserBySteamId_UserExists() {
        String steamId = "12345";
        User user = new User();
        user.setSteamId(steamId);

        when(userRepository.findBySteamId(steamId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(new UserDto().builder().username("testuser").build());

        UserDto result = userService.getUserBySteamId(steamId);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());

        verify(userRepository, times(1)).findBySteamId(steamId);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    void testGetUserBySteamId_UserNotFound() {
        String steamId = "nonexistent";

        when(userRepository.findBySteamId(steamId)).thenReturn(Optional.empty());

        UserDto result = userService.getUserBySteamId(steamId);

        assertNull(result);
        verify(userRepository, times(1)).findBySteamId(steamId);
    }

    @Test
    void testGetBySteamId_UserExists() {
        String steamId = "12345";
        User user = new User();
        user.setSteamId(steamId);

        when(userRepository.findBySteamId(steamId)).thenReturn(Optional.of(user));

        User result = userService.getBySteamId(steamId);

        assertNotNull(result);
        assertEquals(steamId, result.getSteamId());

        verify(userRepository, times(1)).findBySteamId(steamId);
    }

    @Test
    void testGetBySteamId_UserNotFound() {
        String steamId = "nonexistent";
        Exception exception = assertThrows(UserNotFoundException.class,
                () -> userService.getBySteamId(steamId));

        assertEquals("User with steamId=nonexistent not found", exception.getMessage());
    }

}
