package kr.hhplus.be.server.domain.user.repository;

import kr.hhplus.be.server.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface UserRepository {
    Optional<User> findById(Long id);

    Optional<User> findByUserId(Long userId);

    User save(User user);

    List<User> findAll();

    void deleteAll();
}
