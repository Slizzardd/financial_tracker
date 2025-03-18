package ua.com.alevel.configs.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.com.alevel.services.security.JwtService;

import java.io.IOException;

public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    private final JwtService jwtService;

    public JWTAuthenticationFilter(JwtService jwtService, @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        logger.info("Processing request: {}", request.getRequestURI());

            if (shouldNotFilter(request)) {
                filterChain.doFilter(request, response);
                return;
            }

        try {
            String token = getJWTFromRequest(request);

            if (StringUtils.hasText(token) && jwtService.authCheck(token)) {
                String username = jwtService.extractUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                logger.warn("Invalid or missing JWT token");
            }
        } catch (Exception e) {
            logger.warn("Error during validation of JWT token: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }


    private String getJWTFromRequest(HttpServletRequest request) {
        return jwtService.extractToken(request.getHeader("Authorization")).orElse(null);
    }
}
