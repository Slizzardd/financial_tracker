package ua.com.alevel.services;

import ua.com.alevel.persistence.entities.RefreshToken;

public interface RefreshTokenService extends BaseService<RefreshToken> {

    RefreshToken createToken(RefreshToken refreshToken);

    RefreshToken findByToken(String token);

    Boolean isValid(String token, String currentDeviceName, String currentIpAddress);

}
