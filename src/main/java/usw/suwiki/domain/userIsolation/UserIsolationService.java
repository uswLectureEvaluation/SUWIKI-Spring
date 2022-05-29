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


    //아이디 중복 확인
    @Transactional
    public Optional<UserIsolation> existId(String loginId) {
        return userIsolationRepository.findByLoginId(loginId);
    }

    //이메일 중복 확인
    @Transactional
    public Optional<UserIsolation> existEmail(String email) {
        return userIsolationRepository.findByEmail(email);
    }

    //Optional<User> -> User
    @Transactional
    public UserIsolation convertOptionalUserToDomainUser(Optional<UserIsolation> optionalUserIsolation) {
        if (optionalUserIsolation.isPresent()) {
            return optionalUserIsolation.get();
        }
        throw new AccountException(ErrorType.USER_NOT_EXISTS);
    }

    //loginId로 유저 격리 테이블 꺼내오기
    @Transactional
    public Optional<UserIsolation> loadUserFromLoginId(String loginId) {
        return Optional.ofNullable(userIsolationRepository.findByLoginId(loginId).orElseThrow(() -> new AccountException(ErrorType.USER_NOT_EXISTS)));
    }

    //유저 삭제
    @Transactional
    public void deleteIsolationUser(Long idx) {
        userIsolationRepository.deleteById(idx);
    }

    //회원탈퇴 요청 시각이 30일 전인지 확인 (매일 0시에 한번 씩 돌린다.)
    @Transactional
    @Scheduled(cron = "0 0 0 * * *") //매일 0시에 한번 씩 돌린다.
    public void requestQuitDateStamp() {
        LocalDateTime targetTime = LocalDateTime.now().minusDays(30);
        List<UserIsolation> targetUser = userIsolationRepository.findByRequestedQuitDateBefore(targetTime);

        for (int i = 0; i < targetUser.toArray().length; i++) {
            userIsolationRepository.deleteByLoginId(targetUser.get(i).getLoginId());
        }
    }

    //격리 후 3년간 로그인 하지 않는 대상에 지정되기 한달 전에 이메일 보내기
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void autoDeleteTargetIsThreeYearsSendEmail() {
        LocalDateTime targetTime = LocalDateTime.now().minusYears(3).plusDays(30);
        List<UserIsolation> targetUser = userIsolationRepository.findByLastLoginBefore(targetTime);

        for (int i = 0; i < targetUser.toArray().length; i++) {
            emailSender.send(targetUser.get(i).getEmail(), buildAutoDeletedWarningUserFormService.buildEmail());
        }
    }

    //격리 후 3년간 로그인 하지 않으면 계정 자동 삭제
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void autoDeleteTargetIsThreeYears() {
        LocalDateTime targetTime = LocalDateTime.now().minusYears(3);
        List<UserIsolation> targetUser = userIsolationRepository.findByLastLoginBefore(targetTime);

        for (int i = 0; i < targetUser.toArray().length; i++) {
            userIsolationRepository.deleteByLoginId(targetUser.get(i).getLoginId());
        }
    }
}
