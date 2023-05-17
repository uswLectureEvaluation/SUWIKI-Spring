package usw.suwiki.domain.user.userIsolation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.userIsolation.UserIsolation;
import usw.suwiki.domain.user.userIsolation.repository.UserIsolationRepository;
import usw.suwiki.global.exception.errortype.AccountException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static usw.suwiki.global.exception.ExceptionType.USER_NOT_EXISTS;

@Service
@RequiredArgsConstructor
@Transactional
public class UserIsolationCRUDService {

    private final UserIsolationRepository userIsolationRepository;

    public void saveUserIsolation(UserIsolation userIsolation) {
        userIsolationRepository.save(userIsolation);
    }

    @Transactional(readOnly = true)
    public Optional<UserIsolation> loadWrappedUserFromUserIdx(Long userIdx) {
        return userIsolationRepository.findById(userIdx);
    }

    @Transactional(readOnly = true)
    public Optional<UserIsolation> loadWrappedUserFromLoginId(String loginId) {
        return userIsolationRepository.findByLoginId(loginId);
    }

    @Transactional(readOnly = true)
    public Optional<UserIsolation> loadWrappedUserFromEmail(String email) {
        return userIsolationRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<UserIsolation> loadIsolationUsersLastLoginBeforeTargetTime(LocalDateTime targetTime) {
        return userIsolationRepository.findByLastLoginBefore(targetTime);
    }

    @Transactional(readOnly = true)
    public UserIsolation loadUserFromUserIdx(Long userIdx) {
        return convertOptionalUserToDomainUser(userIsolationRepository.findById(userIdx));
    }

    @Transactional(readOnly = true)
    public UserIsolation loadUserFromLoginId(String loginId) {
        return convertOptionalUserToDomainUser(userIsolationRepository.findByLoginId(loginId));
    }

    @Transactional(readOnly = true)
    public UserIsolation loadUserFromEmail(String email) {
        return convertOptionalUserToDomainUser(userIsolationRepository.findByEmail(email));
    }

    public void deleteByUserIdx(Long userIdx) {
        userIsolationRepository.deleteByUserIdx(userIdx);
    }

    public void deleteByLoginId(String loginId) {
        userIsolationRepository.deleteByLoginId(loginId);
    }

    private UserIsolation convertOptionalUserToDomainUser(Optional<UserIsolation> optionalUser) {
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        throw new AccountException(USER_NOT_EXISTS);
    }
}