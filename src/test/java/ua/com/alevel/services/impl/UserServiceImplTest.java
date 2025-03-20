package ua.com.alevel.services.impl;

import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ua.com.alevel.persistence.entities.User;
import ua.com.alevel.persistence.repositories.UserRepository;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_ShouldSaveUser_WhenEmailIsUnique() {
        String email = "test@test.com";
        String rawPassword = "password";
        String hashedPassword = "hashed_password";

        User user = new User();
        user.setEmail(email);
        user.setPassword(rawPassword);

        Mockito.when(userRepository.existsByEmail(email)).thenReturn(false);
        Mockito.when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);

        Mockito.verify(userRepository).existsByEmail(email);
        Mockito.verify(passwordEncoder).encode(rawPassword);
        Mockito.verify(userRepository).save(Mockito.any(User.class));

        Assertions.assertEquals(email, createdUser.getEmail());
        Assertions.assertEquals(hashedPassword, createdUser.getPassword());
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailAlreadyExists() {
        String email = "test@test.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword("password");

        Mockito.when(userRepository.existsByEmail(email)).thenReturn(true);

        EntityExistsException exception = Assertions.assertThrows(
                EntityExistsException.class,
                () -> userService.createUser(user)
        );

        Assertions.assertEquals("A user with this email already exists", exception.getMessage());

        Mockito.verify(passwordEncoder, Mockito.never()).encode(Mockito.anyString());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }
}
