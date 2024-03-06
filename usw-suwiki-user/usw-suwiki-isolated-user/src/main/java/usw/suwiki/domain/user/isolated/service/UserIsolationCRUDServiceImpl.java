package usw.suwiki.domain.user.isolated.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.isolated.UserIsolation;
import usw.suwiki.domain.user.isolated.UserIsolationRepository;
import usw.suwiki.domain.user.service.UserCRUDService;
import usw.suwiki.domain.user.service.UserIsolationCRUDService;
import usw.suwiki.secure.encode.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class UserIsolationCRUDServiceImpl implements UserIsolationCRUDService {
    private final UserIsolationRepository userIsolationRepository;

    @Override
    public boolean isIsolatedByEmail(String email) {
        return userIsolationRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean isIsolatedByLoginId(String loginId) {
        return userIsolationRepository.findByLoginId(loginId).isPresent();
    }

    @Override
    public Optional<String> getIsolatedLoginIdByEmail(String email) {
        return loadWrappedUserFromEmail(email)
          .map(UserIsolation::getLoginId);
    }

    @Override
    public boolean isRetrievedUserEquals(String email, String loginId) {
        Optional<UserIsolation> byEmail = userIsolationRepository.findByEmail(email);
        Optional<UserIsolation> byLoginId = userIsolationRepository.findByLoginId(loginId);

        return byEmail.isPresent() && byLoginId.isPresent() && byEmail.equals(byLoginId);
    }

    @Override
    @Transactional
    public String updateIsolatedUserPassword(PasswordEncoder passwordEncoder, String email) {
        return userIsolationRepository.findByEmail(email)
          .map(it -> it.updateRandomPassword(passwordEncoder))
          .orElseThrow(() -> new AccountException(ExceptionType.USER_NOT_EXISTS));
    }

    @Override
    public boolean isLoginableIsolatedUser(String loginId, String inputPassword, PasswordEncoder passwordEncoder) {
        return userIsolationRepository.findByLoginId(loginId)
          .map(it -> it.validatePassword(passwordEncoder, inputPassword))
          .orElseThrow(() -> new AccountException(ExceptionType.USER_NOT_EXISTS));
    }

    @Override
    @Transactional
    public User awakeIsolated(UserCRUDService userCRUDService, String loginId) {
        UserIsolation userIsolation = userIsolationRepository.findByLoginId(loginId)
          .orElseThrow(() -> new AccountException(ExceptionType.USER_NOT_EXISTS));

        User user = userCRUDService.loadUserFromUserIdx(userIsolation.getUserIdx());
        user.awake(userIsolation.getLoginId(), userIsolation.getPassword(), userIsolation.getEmail());

        userIsolationRepository.deleteByLoginId(loginId);
        return user;
    }

    @Transactional
    public void saveUserIsolation(UserIsolation userIsolation) {
        userIsolationRepository.save(userIsolation);
    }

    @Transactional
    public void deleteByUserIdx(Long userIdx) {
        userIsolationRepository.deleteByUserIdx(userIdx);
    }

    @Transactional
    public void deleteByLoginId(String loginId) {
        userIsolationRepository.deleteByLoginId(loginId);
    }

    public Optional<UserIsolation> loadWrappedUserFromUserIdx(Long userIdx) {
        return userIsolationRepository.findById(userIdx);
    }

    public Optional<UserIsolation> loadWrappedUserFromLoginId(String loginId) {
        return userIsolationRepository.findByLoginId(loginId);
    }

    public Optional<UserIsolation> loadWrappedUserFromEmail(String email) {
        return userIsolationRepository.findByEmail(email);
    }

    public List<UserIsolation> loadIsolationUsersLastLoginBeforeTargetTime(LocalDateTime targetTime) {
        return userIsolationRepository.findByLastLoginBefore(targetTime);
    }

    public UserIsolation loadUserFromUserIdx(Long userIdx) {
        return convertOptionalUserToDomainUser(userIsolationRepository.findById(userIdx));
    }

    public UserIsolation loadUserFromLoginId(String loginId) {
        return convertOptionalUserToDomainUser(userIsolationRepository.findByLoginId(loginId));
    }

    public UserIsolation loadUserFromEmail(String email) {
        return convertOptionalUserToDomainUser(userIsolationRepository.findByEmail(email));
    }

    public long countAllIsolatedUsers() {
        return userIsolationRepository.count();
    }

    private UserIsolation convertOptionalUserToDomainUser(Optional<UserIsolation> optionalUser) {
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        throw new AccountException(ExceptionType.USER_NOT_EXISTS);
    }
}
