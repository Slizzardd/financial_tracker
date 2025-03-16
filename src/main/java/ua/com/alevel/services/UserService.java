package ua.com.alevel.services;

import ua.com.alevel.persistence.entities.User;

public interface UserService extends BaseService<User> {

    User createUser(User user);

    User findByEmail(String email);
}
