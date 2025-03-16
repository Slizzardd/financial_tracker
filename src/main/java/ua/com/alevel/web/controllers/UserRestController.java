package ua.com.alevel.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.com.alevel.facades.UserFacade;
import ua.com.alevel.web.dto.requests.UserRequestDto;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    private final UserFacade userFacade;

    public UserRestController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserRequestDto userRequestDto) {

        return ResponseEntity.ok(userFacade.createUser(userRequestDto));
    }

}
