package ua.com.alevel.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.com.alevel.exceptions.InvalidTokenException;
import ua.com.alevel.persistence.entities.RefreshToken;
import ua.com.alevel.persistence.repositories.RefreshTokenRepository;
import ua.com.alevel.services.RefreshTokenService;
import ua.com.alevel.services.security.JwtService;

import java.time.Instant;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMillis;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    @Override
    public RefreshToken createToken(RefreshToken refreshToken) {
        logger.info("Creating refresh token for user: {}", refreshToken.getUser().getEmail());

        deleteExpiredTokensForUser(refreshToken);

        refreshToken.setToken(jwtService.generateRefreshToken(refreshToken.getUser().getEmail()));
        refreshToken.setExpiresAt(Instant.now().plusSeconds(refreshExpirationMillis));

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        logger.info("Refresh token created successfully for user: {}", refreshToken.getUser().getEmail());
        return savedToken;
    }

    @Override
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    logger.warn("Refresh token not found: {}", token);
                    return new InvalidTokenException("Invalid or expired refresh token");
                });
    }

    @Override
    public Boolean isValid(String token, String currentDeviceName, String currentIpAddress) {
        RefreshToken refreshToken = findByToken(token);

        if (isExpired(refreshToken)) {
            logger.warn("Refresh token is expired for user: {}", refreshToken.getUser().getEmail());
            return false;
        }

        boolean validDevice = deviceMatches(refreshToken, currentDeviceName);
        boolean validIp = ipMatches(refreshToken, currentIpAddress);

        return validDevice && validIp;
    }

    private boolean deviceMatches(RefreshToken token, String currentDeviceName) {
        return token.getDeviceName() != null && token.getDeviceName().equals(currentDeviceName);
    }

    private boolean ipMatches(RefreshToken token, String currentIpAddress) {
        return token.getIpAddress() != null && token.getIpAddress().equals(currentIpAddress);
    }

    private boolean isExpired(RefreshToken token) {
        return token.getExpiresAt().isBefore(Instant.now());
    }

    private void deleteExpiredTokensForUser(RefreshToken refreshToken) {
        refreshTokenRepository.findAllByUser(refreshToken.getUser()).forEach(token -> {
            if (deviceMatches(token, refreshToken.getDeviceName()) && ipMatches(token, refreshToken.getIpAddress())) {
                logger.info("Deleting expired refresh token for user: {}", token.getUser().getEmail());
                refreshTokenRepository.delete(token);
            }
        });
    }
}
