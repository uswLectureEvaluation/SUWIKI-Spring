package usw.suwiki.domain.user.userIsolation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.confirmationtoken.repository.ConfirmationTokenRepository;
import usw.suwiki.domain.confirmationtoken.service.EmailSender;
import usw.suwiki.domain.evaluation.service.EvaluatePostsService;
import usw.suwiki.domain.exam.service.ExamPostsService;
import usw.suwiki.domain.favoritemajor.service.FavoriteMajorService;
import usw.suwiki.domain.postreport.repository.EvaluateReportRepository;
import usw.suwiki.domain.postreport.repository.ExamReportRepository;
import usw.suwiki.domain.refreshToken.repository.RefreshTokenRepository;
import usw.suwiki.domain.user.user.entity.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.domain.user.user.repository.RestrictingUserRepository;
import usw.suwiki.domain.user.user.service.UserService;
import usw.suwiki.domain.user.userIsolation.entity.UserIsolation;
import usw.suwiki.domain.user.userIsolation.repository.UserIsolationRepository;
import usw.suwiki.domain.viewExam.service.ViewExamService;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.util.emailBuild.BuildSoonDormantTargetForm;
import usw.suwiki.global.util.emailBuild.UserAutoDeletedWarningForm;

import java.time.LocalDateTime;
import java.util.List;

import static usw.suwiki.global.exception.ErrorType.PASSWORD_ERROR;
import static usw.suwiki.global.exception.ErrorType.USER_NOT_EXISTS;

@Service
@RequiredArgsConstructor
@Transactional
public class UserIsolationService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final FavoriteMajorService favoriteMajorService;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserIsolationRepository userIsolationRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final BuildSoonDormantTargetForm buildSoonDormantTargetForm;
    private final UserAutoDeletedWarningForm userAutoDeletedWarningForm;
    private final EmailSender emailSender;
    private final RestrictingUserRepository restrictingUserRepository;
    private final ExamReportRepository examReportRepository;
    private final EvaluateReportRepository evaluateReportRepository;
    private final EvaluatePostsService evaluatePostsService;
    private final ExamPostsService examPostsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ViewExamService viewExamService;

    public UserIsolation loadUserFromLoginId(String loginId) {
        return userIsolationRepository.findByLoginId(loginId)
                .orElseThrow(() -> new AccountException(USER_NOT_EXISTS));
    }

    public void convertToIsolationUser(User user) {
        UserIsolation userIsolation = UserIsolation.builder()
                .userIdx(user.getId())
                .loginId(user.getLoginId())
                .password(user.getPassword())
                .email(user.getEmail())
                .lastLogin(user.getLastLogin())
                .requestedQuitDate(user.getRequestedQuitDate())
                .build();
        userIsolationRepository.save(userIsolation);

        userRepository.applyUserSoftDelete(user.getId());
    }

    public boolean validatePasswordAtIsolationTable(String loginId, String password) {
        return bCryptPasswordEncoder.matches(password, userIsolationRepository.findByLoginId(loginId).get().getPassword());
    }

    public User sleepingUserLogin(String loginId, String password) {
        UserIsolation userIsolation = loadUserFromLoginId(loginId);
        if (validatePasswordAtIsolationTable(loginId, password)) {
            userRepository.unapplyUserSoftDelete(userIsolation.getUserIdx(), userIsolation);
            userIsolationRepository.deleteByLoginId(loginId);
        } else {
            throw new AccountException(PASSWORD_ERROR);
        }
        return userService.loadUserFromLoginId(loginId);
    }

    @Scheduled(cron = "2 0 0 * * *")
    public void sendEmailSoonDormant() {
        LocalDateTime targetTime = LocalDateTime.now().minusMonths(11);
        List<User> user = userRepository.findByLastLoginBefore(targetTime);
        for (int i = 0; i < user.toArray().length; i++) {
            if (user.get(i).getEmail() != null) {
                emailSender.send(user.get(i).getEmail(), buildSoonDormantTargetForm.buildEmail());
            }
        }
    }

    @Scheduled(cron = "4 0 0 * * *")
    public void convertSleepingTable() {
        LocalDateTime targetTime = LocalDateTime.now().minusMonths(12);
        List<User> targetUser = userRepository.findByLastLoginBefore(targetTime);
        for (int i = 0; i < targetUser.toArray().length; i++) {
            if (userIsolationRepository.findByUserIdx(targetUser.get(i).getId()).isEmpty()) {
                convertToIsolationUser(targetUser.get(i));
            }
        }
    }

    @Scheduled(cron = "6 0 0 * * *")
    public void autoDeleteTargetIsThreeYearsSendEmail() {
        LocalDateTime targetTime = LocalDateTime.now().minusYears(3).plusDays(30);
        List<UserIsolation> targetUser = userIsolationRepository.findByLastLoginBefore(targetTime);
        for (int i = 0; i < targetUser.toArray().length; i++) {
            emailSender.send(targetUser.get(i).getEmail(), userAutoDeletedWarningForm.buildEmail());
        }
    }

    // 3년간 로그인 하지 않으면 계정 자동 삭제
    @Scheduled(cron = "8 0 0 * * *")
    public void autoDeleteTargetIsThreeYears() {
        LocalDateTime targetTime = LocalDateTime.now().minusYears(3);
        List<UserIsolation> targetUser = userIsolationRepository.findByLastLoginBefore(targetTime);
        for (int i = 0; i < targetUser.toArray().length; i++) {
            viewExamService.deleteByUserIdx(targetUser.get(i).getUserIdx());
            refreshTokenRepository.deleteByUserIdx(targetUser.get(i).getUserIdx());
            examReportRepository.deleteByReportedUserIdx(targetUser.get(i).getUserIdx());
            examReportRepository.deleteByReportingUserIdx(targetUser.get(i).getUserIdx());
            evaluateReportRepository.deleteByReportingUserIdx(targetUser.get(i).getUserIdx());
            evaluateReportRepository.deleteByReportedUserIdx(targetUser.get(i).getUserIdx());
            evaluatePostsService.deleteByUser(targetUser.get(i).getUserIdx());
            examPostsService.deleteByUser(targetUser.get(i).getUserIdx());
            favoriteMajorService.deleteAllByUser(targetUser.get(i).getUserIdx());
            restrictingUserRepository.deleteByUserIdx(targetUser.get(i).getUserIdx());
            confirmationTokenRepository.deleteByUserIdx(targetUser.get(i).getUserIdx());
            userIsolationRepository.deleteByLoginId(targetUser.get(i).getLoginId());
            userRepository.deleteById(targetUser.get(i).getUserIdx());
        }
    }
}