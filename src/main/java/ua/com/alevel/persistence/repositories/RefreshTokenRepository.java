package ua.com.alevel.persistence.repositories;

import org.springframework.stereotype.Repository;
import ua.com.alevel.persistence.entities.RefreshToken;
import ua.com.alevel.persistence.entities.User;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RefreshTokenRepository extends BaseRepository<RefreshToken> {

    Optional<RefreshToken> findByToken(String token);

    Set<RefreshToken> findAllByUser(User user);

    void deleteAllByUser(User user);
}
