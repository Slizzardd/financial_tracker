package ua.com.alevel.facades.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.com.alevel.facades.UserFacade;
import ua.com.alevel.persistence.entities.User;
import ua.com.alevel.services.UserService;
import ua.com.alevel.web.dto.requests.UserRequestDto;
import ua.com.alevel.web.dto.responses.UserResponseDto;

@Service
public class UserFacadeImpl implements UserFacade {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserFacadeImpl.class);
    public UserFacadeImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserResponseDto createUser(UserRequestDto req) {
        User user = new User();
        fillUserFromDto(req, user);
        user.setPassword(req.getPassword());
        return new UserResponseDto(userService.createUser(user));
    }


    private void fillUserFromDto(UserRequestDto requestDto, User user){
        user.setEmail(requestDto.getEmail());
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
    }
}
