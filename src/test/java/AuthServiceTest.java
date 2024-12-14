import com.vkr.user_service.repository.token.JwtRefreshTokenRepository;
import com.vkr.user_service.service.auth.AuthServiceImpl;
import com.vkr.user_service.service.blacklist.BlacklistService;
import com.vkr.user_service.service.jwt.JwtGenerator;
import com.vkr.user_service.service.user.UserService;
import com.vkr.user_service.util.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private JwtGenerator jwtAccessGenerator;

    @Mock
    private JwtGenerator jwtRefreshGenerator;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtRefreshTokenRepository jwtRefreshTokenRepository;

    @Mock
    private BlacklistService blacklistService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void logout_ShouldAddTokenToBlacklist() {
        // Arrange
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(jwtUtil.extractTokenFromRequestHeader(mockRequest)).thenReturn("test-token");

        // Act
        authService.logout(mockRequest);

        // Assert
        verify(blacklistService, times(1)).addToBlacklist("test-token");
    }

    @Test
    void extractSteamId_ShouldReturnSteamId() {
        // Arrange
        String claimedId = "https://steamcommunity.com/openid/id/123456";

        // Act
        String steamId = authService.extractSteamId(claimedId);

        // Assert
        assertEquals("123456", steamId);
    }
}
