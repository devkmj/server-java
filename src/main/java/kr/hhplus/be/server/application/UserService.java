package kr.hhplus.be.server.application;

import org.springframework.transaction.annotation.Transactional;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void charge(Long userId, Integer amount) {
        User user = getUser(userId);
        user.charge(amount);
    }

    @Transactional
    public void deduct(Long userId, Integer amount) {
        User user = getUser(userId);
        user.deduct(amount);
    }

    @Transactional(readOnly = true)
    public int getBalance(Long userId) {
        return getUser(userId).getBalance();
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
    }
}
