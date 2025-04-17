package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.balance.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);

    Optional<User> findByUserId(Long userId);

    User save(User user);

    List<User> findAll();

    void deleteAll();
}
