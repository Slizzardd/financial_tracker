package ua.com.alevel.facades.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.data.util.Pair;
import ua.com.alevel.exceptions.InvalidTokenException;
import ua.com.alevel.persistence.entities.RefreshToken;
import ua.com.alevel.persistence.entities.User;
import ua.com.alevel.persistence.types.Role;
import ua.com.alevel.services.RefreshTokenService;
import ua.com.alevel.services.UserService;
import ua.com.alevel.services.security.JwtService;
import ua.com.alevel.web.dto.requests.AuthRequestDto;
import ua.com.alevel.web.dto.responses.AuthResponseDto;
import ua.com.alevel.web.dto.responses.UserResponseDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthFacadeImplTest {

    @Mock
    private UserService userService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AuthFacadeImpl authFacade;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void authenticate_ShouldReturnTokens_WhenCredentialsAreValid() {
        AuthRequestDto request = new AuthRequestDto();
        request.setIpAddress("127.0.0.1");
        request.setPassword("password");
        request.setEmail("test@test.com");
        request.setDeviceName("PC");

        User user = new User();
        user.setEmail("test@test.com");
        user.setRole(Role.USER);

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userService.findByEmail(request.getEmail())).thenReturn(user);
        when(jwtService.generateAccessJwtToken(any(UserResponseDto.class))).thenReturn("mockedAccessToken");

        RefreshToken mockToken = new RefreshToken();
        mockToken.setToken("mockedRefreshToken");
        mockToken.setUser(user);
        when(refreshTokenService.createToken(any(RefreshToken.class))).thenReturn(mockToken);

        Pair<AuthResponseDto, String> response = authFacade.authenticate(request);

        assertNotNull(response);
        assertEquals("mockedAccessToken", response.getFirst().getAccessToken());
        assertEquals("mockedRefreshToken", response.getSecond());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService).findByEmail(request.getEmail());

        ArgumentCaptor<UserResponseDto> userCaptor = ArgumentCaptor.forClass(UserResponseDto.class);
        verify(jwtService).generateAccessJwtToken(userCaptor.capture());
        assertEquals("test@test.com", userCaptor.getValue().getEmail());
        assertEquals(Role.USER, userCaptor.getValue().getRole());

        ArgumentCaptor<RefreshToken> refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenService).createToken(refreshTokenCaptor.capture());
        assertNotNull(refreshTokenCaptor.getValue());
    }

    @Test
    void authenticate_ShouldThrowException_WhenCredentialsAreInvalid() {
        AuthRequestDto request = new AuthRequestDto();
        request.setIpAddress("127.0.0.1");
        request.setPassword("password");
        request.setEmail("test@test.com");
        request.setDeviceName("PC");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> authFacade.authenticate(request));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, never()).findByEmail(anyString());
        verify(jwtService, never()).generateAccessJwtToken(any());
        verify(refreshTokenService, never()).createToken(any());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void refreshToken_ShouldReturnNewAccessToken_WhenTokenIsValid() {
        String oldRefreshToken = "valid-refresh-token";
        String newAccessToken = "new-access-token";
        String deviceName = "PC";
        String ipAddress = "127.0.0.1";

        User user = new User();
        user.setEmail("test@test.com");
        user.setRole(Role.USER);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(oldRefreshToken);
        refreshToken.setUser(user);

        when(refreshTokenService.findByToken(oldRefreshToken)).thenReturn(refreshToken);
        when(refreshTokenService.isValid(oldRefreshToken, deviceName, ipAddress)).thenReturn(true);
        when(jwtService.generateAccessJwtToken(any(UserResponseDto.class))).thenReturn(newAccessToken);

        AuthResponseDto response = authFacade.refreshToken(oldRefreshToken, deviceName, ipAddress);

        assertNotNull(response);
        assertEquals(newAccessToken, response.getAccessToken());

        verify(refreshTokenService).findByToken(oldRefreshToken);
        verify(refreshTokenService).isValid(oldRefreshToken, deviceName, ipAddress);

        ArgumentCaptor<UserResponseDto> userCaptor = ArgumentCaptor.forClass(UserResponseDto.class);
        verify(jwtService).generateAccessJwtToken(userCaptor.capture());
        assertEquals("test@test.com", userCaptor.getValue().getEmail());
        assertEquals(Role.USER, userCaptor.getValue().getRole());
    }

    @Test
    void refreshToken_ShouldThrowException_WhenTokenIsInvalid() {
        String invalidToken = "invalid-refresh-token";
        String deviceName = "PC";
        String ipAddress = "127.0.0.1";

        when(refreshTokenService.findByToken(invalidToken)).thenReturn(null);

        assertThrows(InvalidTokenException.class,
                () -> authFacade.refreshToken(invalidToken, deviceName, ipAddress));

        verify(refreshTokenService).findByToken(invalidToken);
        verify(refreshTokenService, never()).isValid(anyString(), anyString(), anyString());
        verify(jwtService, never()).generateAccessJwtToken(any());
    }
}
