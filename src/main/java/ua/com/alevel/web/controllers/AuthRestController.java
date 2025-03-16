package ua.com.alevel.web.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.alevel.facades.AuthFacade;
import ua.com.alevel.utils.AuthRequestEnricher;
import ua.com.alevel.web.dto.requests.AuthRequestDto;
import ua.com.alevel.web.dto.responses.AuthResponseDto;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {

    private final AuthFacade authFacade;
    private final AuthRequestEnricher authRequestEnricher;
    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMillis;

    public AuthRestController(AuthFacade authFacade, AuthRequestEnricher authRequestEnricher) {
        this.authFacade = authFacade;
        this.authRequestEnricher = authRequestEnricher;
    }


    @PostMapping("/login")
    public ResponseEntity<?> authentication(@RequestBody AuthRequestDto authRequestDto, HttpServletRequest request) {
        AuthRequestDto enrichedDto = authRequestEnricher.enrich(authRequestDto, request);

        Pair<AuthResponseDto, String> authResponse = authFacade.authenticate(enrichedDto);


        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", authResponse.getSecond())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshExpirationMillis)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(authResponse.getFirst());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletRequest request) {
        AuthResponseDto responseDto = authFacade.refreshToken(
                refreshToken
                , authRequestEnricher.getDeviceName(request)
                , authRequestEnricher.getIp(request));

        return ResponseEntity.ok(responseDto);
    }
}
