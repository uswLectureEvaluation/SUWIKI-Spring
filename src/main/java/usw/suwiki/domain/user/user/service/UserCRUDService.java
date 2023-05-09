package usw.suwiki.domain.user.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.global.exception.errortype.AccountException;

import java.util.Optional;

import static usw.suwiki.global.exception.ExceptionType.USER_NOT_EXISTS;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCRUDService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User loadUserFromUserIdx(Long userIdx) {
        return convertOptionalUserToDomainUser(userRepository.findById(userIdx));
    }

    @Transactional(readOnly = true)
    public User loadUserFromLoginId(String loginId) {
        return convertOptionalUserToDomainUser(userRepository.findByLoginId(loginId));
    }

    private User convertOptionalUserToDomainUser(Optional<User> optionalUser) {
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        throw new AccountException(USER_NOT_EXISTS);
    }

    @Transactional(readOnly = true)
    public int findAllUsersSize() {
        return userRepository.findAll().size();
    }

    public void deleteFromUserIdx(Long userIdx) {
        userRepository.deleteById(userIdx);
    }
}
