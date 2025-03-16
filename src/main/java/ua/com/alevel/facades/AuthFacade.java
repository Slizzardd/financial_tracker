package ua.com.alevel.facades;

import org.springframework.data.util.Pair;
import ua.com.alevel.web.dto.requests.AuthRequestDto;
import ua.com.alevel.web.dto.responses.AuthResponseDto;

public interface AuthFacade extends BaseFacade<AuthRequestDto, AuthResponseDto> {

    Pair<AuthResponseDto, String> authenticate(AuthRequestDto request);

    AuthResponseDto refreshToken(String refreshToken, String currDeviceName, String currIpAddress);
}
