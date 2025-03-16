package ua.com.alevel.services.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.com.alevel.web.dto.responses.UserResponseDto;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@Component
public class JwtService {

    private static final Logger LOGGER = Logger.getLogger(JwtService.class.getName());

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expiration}")
    private long accessExpirationMillis;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMillis;

    private Key signingKey;

    @PostConstruct
    public void init() {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String generateAccessJwtToken(UserResponseDto user) {
        return generateToken(user.getEmail(), Duration.ofMillis(accessExpirationMillis),
                Map.of("Role", user.getRole().name()));
    }

    public String generateRefreshToken(String username) {
        return generateToken(username, Duration.ofMillis(refreshExpirationMillis), Map.of());
    }

    public boolean authCheck(String jwtToken) {
        try {
            parseClaims(jwtToken);
            return true;
        } catch (ExpiredJwtException e) {
            LOGGER.warning("Token expired: " + e.getMessage());
        } catch (MalformedJwtException | SignatureException e) {
            LOGGER.warning("Invalid token: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.warning("Token validation error: " + e.getMessage());
        }
        return false;
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public Optional<String> extractToken(String fullJwtToken) {
        return Optional.ofNullable(fullJwtToken)
                .filter(token -> token.startsWith("Bearer "))
                .map(token -> token.substring(7));
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String generateToken(String subject, Duration expiration, Map<String, Object> claims) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + expiration.toMillis()))
                .addClaims(claims)
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }
}
