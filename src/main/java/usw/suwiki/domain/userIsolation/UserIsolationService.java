package usw.suwiki.domain.userIsolation;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.email.EmailSender;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.domain.emailBuild.BuildAutoDeletedWarningUserFormService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserIsolationService {

    private final UserIsolationRepository userIsolationRepository;


    private final EmailSender emailSender;
    private final BuildAutoDeletedWarningUserFormService buildAutoDeletedWarningUserFormService;


    // Optional<User> -> User
    @Transactional
    public UserIsolation convertOptionalUserToDomainUser(Optional<UserIsolation> optionalUserIsolation) {
        if (optionalUserIsolation.isPresent()) {
            return optionalUserIsolation.get();
        }
        throw new AccountException(ErrorType.USER_NOT_EXISTS);
    }

    //loginId로 유저 격리 테이블 꺼내오기
    @Transactional
    public UserIsolation loadUserFromLoginId(String loginId) {
        return convertOptionalUserToDomainUser(userIsolationRepository.findByLoginId(loginId));
    }

    @Transactional
    public void isRestricted(String loginId) {
        if (userIsolationRepository.loadUserRestriction(loginId)) throw new AccountException(ErrorType.USER_RESTRICTED);
    }


    //회원탈퇴 요청 시각이 30일 전인지 확인 (매일 0시마다 검사한다.)
    @Transactional
//    @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "0 * * * * *")
    public void requestQuitDateStamp() {
//        LocalDateTime targetTime = LocalDateTime.now().minusDays(30);
        LocalDateTime targetTime = LocalDateTime.now().minusMinutes(10);
        List<UserIsolation> targetUser = userIsolationRepository.findByRequestedQuitDateBefore(targetTime);

        for (int i = 0; i < targetUser.toArray().length; i++) {
            userIsolationRepository.deleteByLoginId(targetUser.get(i).getLoginId());
        }
    }


}
