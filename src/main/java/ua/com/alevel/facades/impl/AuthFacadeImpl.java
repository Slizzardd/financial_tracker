package ua.com.alevel.facades.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ua.com.alevel.exceptions.InvalidTokenException;
import ua.com.alevel.facades.AuthFacade;
import ua.com.alevel.persistence.entities.RefreshToken;
import ua.com.alevel.persistence.entities.User;
import ua.com.alevel.services.RefreshTokenService;
import ua.com.alevel.services.UserService;
import ua.com.alevel.services.security.JwtService;
import ua.com.alevel.web.dto.requests.AuthRequestDto;
import ua.com.alevel.web.dto.responses.AuthResponseDto;
import ua.com.alevel.web.dto.responses.UserResponseDto;
@Service
public class AuthFacadeImpl implements AuthFacade {

    private static final Logger logger = LoggerFactory.getLogger(AuthFacadeImpl.class);

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthFacadeImpl(UserService userService, RefreshTokenService refreshTokenService,
                          AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public Pair<AuthResponseDto, String> authenticate(AuthRequestDto request) {
        logger.info("Authenticating user: {}", request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userService.findByEmail(request.getEmail());
        UserResponseDto userResponseDto = new UserResponseDto(user);

        String accessToken = jwtService.generateAccessJwtToken(userResponseDto);
        String refreshToken = generateAndStoreRefreshToken(user, request.getIpAddress(), request.getDeviceName());

        logger.info("User {} authenticated successfully", request.getEmail());

        return Pair.of(new AuthResponseDto(accessToken), refreshToken);
    }

    @Override
    public AuthResponseDto refreshToken(String refreshToken, String currDeviceName, String currIpAddress) {
        logger.info("Refreshing access token...");

        RefreshToken token = refreshTokenService.findByToken(refreshToken);

        if (token == null || !refreshTokenService.isValid(refreshToken, currDeviceName, currIpAddress)) {
            logger.warn("Invalid or expired refresh token: {}", refreshToken);
            throw new InvalidTokenException("The refresh token is invalid or expired");
        }

        UserResponseDto userResponseDto = new UserResponseDto(token.getUser());
        String newAccessToken = jwtService.generateAccessJwtToken(userResponseDto);

        logger.info("Access token refreshed successfully for user {}", token.getUser().getEmail());

        return new AuthResponseDto(newAccessToken);
    }

    private String generateAndStoreRefreshToken(User user, String ipAddress, String deviceName) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setIpAddress(ipAddress);
        refreshToken.setDeviceName(deviceName);
        return refreshTokenService.createToken(refreshToken).getToken();
    }
}
