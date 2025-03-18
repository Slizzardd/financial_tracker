package ua.com.alevel.services.impl;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ua.com.alevel.persistence.entities.User;
import ua.com.alevel.persistence.repositories.UserRepository;
import ua.com.alevel.services.UserService;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User createUser(User user) {
        if (!userRepository.existsByEmail(user.getEmail())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        } else {
            throw new EntityExistsException("A user with this email already exists");
        }
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User update(User user) {
        if (userRepository.existsById(user.getId()) && userRepository.existsByEmail(user.getEmail())) {
            return userRepository.save(user);
        } else {
            throw new EntityNotFoundException("A user with this email doesn`t exists");
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found."));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " not found."));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<User> findAll() {
        return userRepository.findAll();
    }

}
