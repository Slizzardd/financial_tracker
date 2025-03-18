package ua.com.alevel.services;

import ua.com.alevel.persistence.entities.User;

import java.util.List;

public interface UserService extends BaseService<User> {

    User createUser(User user);

    User update(User user);

    User findById(Long id);

    User findByEmail(String email);

    List<User> findAll();

}
