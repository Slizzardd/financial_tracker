package ua.com.alevel.facades;

import ua.com.alevel.web.dto.requests.UserRequestDto;
import ua.com.alevel.web.dto.responses.UserResponseDto;

public interface UserFacade extends BaseFacade<UserRequestDto, UserResponseDto> {

    UserResponseDto createUser(UserRequestDto req);
}
