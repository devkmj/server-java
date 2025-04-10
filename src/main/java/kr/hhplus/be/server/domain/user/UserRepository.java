package kr.hhplus.be.server.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByName(String username);
    Optional<User> findById(Long id);
}
