package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long userId);
}
