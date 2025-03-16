package ua.com.alevel.persistence.repositories;

import org.springframework.stereotype.Repository;
import ua.com.alevel.persistence.entities.User;

import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User> {
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);
}
