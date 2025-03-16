package ua.com.alevel.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import ua.com.alevel.web.dto.requests.AuthRequestDto;

@Component
public class AuthRequestEnricher {

    public AuthRequestDto enrich(AuthRequestDto authRequestDto, HttpServletRequest request) {
        authRequestDto.setIpAddress(getIp(request));
        authRequestDto.setDeviceName(getDeviceName(request));
        return authRequestDto;
    }

    public String getIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }

        return ipAddress;
    }

    public String getDeviceName(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

}
