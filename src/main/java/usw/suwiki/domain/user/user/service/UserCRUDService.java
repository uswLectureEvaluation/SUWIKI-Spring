package usw.suwiki.domain.user.user.service;

import static usw.suwiki.global.exception.ExceptionType.USER_NOT_EXISTS;
import static usw.suwiki.global.exception.ExceptionType.USER_NOT_FOUND;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.global.exception.errortype.AccountException;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCRUDService {

    private final UserRepository userRepository;

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public List<User> loadUsersLastLoginBetweenStartEnd(LocalDateTime startTime, LocalDateTime endTime) {
        return userRepository.findByLastLoginBetween(startTime, endTime);
    }

    @Transactional(readOnly = true)
    public User loadUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Optional<User> loadWrappedUserFromUserIdx(Long userIdx) {
        return userRepository.findById(userIdx);
    }

    @Transactional(readOnly = true)
    public Optional<User> loadWrappedUserFromLoginId(String loginId) {
        return userRepository.findByLoginId(loginId);
    }

    @Transactional(readOnly = true)
    public Optional<User> loadWrappedUserFromEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public User loadUserFromUserIdx(Long userIdx) {
        return convertOptionalUserToDomainUser(userRepository.findById(userIdx));
    }

    @Transactional(readOnly = true)
    public User loadUserFromLoginId(String loginId) {
        return convertOptionalUserToDomainUser(userRepository.findByLoginId(loginId));
    }

    @Transactional(readOnly = true)
    public User loadUserFromEmail(String email) {
        return convertOptionalUserToDomainUser(userRepository.findByEmail(email));
    }

    private User convertOptionalUserToDomainUser(Optional<User> optionalUser) {
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        throw new AccountException(USER_NOT_EXISTS);
    }

    @Transactional(readOnly = true)
    public long countAllUsers() {
        return userRepository.count();
    }

    public void deleteFromUserIdx(Long userIdx) {
        userRepository.deleteById(userIdx);
    }

    public void softDeleteForIsolation(Long userIdx) {
        User user = loadUserFromUserIdx(userIdx);
        user.sleep();
    }
}
