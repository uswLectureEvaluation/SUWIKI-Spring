package usw.suwiki.domain.user.userIsolation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.confirmationtoken.service.ConfirmationTokenCRUDService;
import usw.suwiki.domain.evaluatepost.service.EvaluatePostCRUDService;
import usw.suwiki.domain.exampost.service.ExamPostCRUDService;
import usw.suwiki.domain.favoritemajor.service.FavoriteMajorService;
import usw.suwiki.domain.userlecture.viewexam.service.ViewExamCRUDService;
import usw.suwiki.domain.postreport.service.ReportPostService;
import usw.suwiki.domain.refreshtoken.service.RefreshTokenCRUDService;
import usw.suwiki.domain.restrictinguser.service.RestrictingUserService;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.service.UserCRUDService;
import usw.suwiki.domain.user.userIsolation.UserIsolation;
import usw.suwiki.global.util.emailBuild.BuildSoonDormantTargetForm;
import usw.suwiki.global.util.emailBuild.UserAutoDeletedWarningForm;
import usw.suwiki.global.util.mailsender.EmailSender;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserIsolationSchedulingService {

    private final UserCRUDService userCRUDService;
    private final RestrictingUserService restrictingUserService;
    private final RefreshTokenCRUDService refreshTokenCRUDService;
    private final ReportPostService reportPostService;
    private final ConfirmationTokenCRUDService confirmationTokenCRUDService;
    private final FavoriteMajorService favoriteMajorService;
    private final UserIsolationCRUDService userIsolationCRUDService;
    private final BuildSoonDormantTargetForm buildSoonDormantTargetForm;
    private final UserAutoDeletedWarningForm userAutoDeletedWarningForm;
    private final EmailSender emailSender;
    private final EvaluatePostCRUDService evaluatePostCRUDService;
    private final ViewExamCRUDService viewExamCRUDService;
    private final ExamPostCRUDService examPostCRUDService;

    @Scheduled(cron = "2 0 0 * * *")
    public void sendEmailAboutSleeping() {
        log.info("{} - 휴면 계정 대상들에게 이메일 전송 시작", LocalDateTime.now());
        LocalDateTime startTime = LocalDateTime.now().minusMonths(11).minusDays(1);
        LocalDateTime endTime = LocalDateTime.now().minusMonths(11);
        List<User> users = userCRUDService.loadUsersLastLoginBetweenStartEnd(startTime, endTime);
        for (User user : users) {
            emailSender.send(user.getEmail(), buildSoonDormantTargetForm.buildEmail());
        }
        log.info("{} - 휴면 계정 대상들에게 이메일 전송 종료", LocalDateTime.now());
    }

    @Scheduled(cron = "4 0 0 * * *")
    public void convertSleepingTable() {
        log.info("{} - 휴면 계정 전환 시작", LocalDateTime.now());
        LocalDateTime startTime = LocalDateTime.now().minusMonths(35);
        LocalDateTime endTime = LocalDateTime.now().minusMonths(12);
        List<User> users = userCRUDService.loadUsersLastLoginBetweenStartEnd(startTime, endTime);
        for (User user : users) {
            if (userIsolationCRUDService.loadUserFromUserIdx(user.getId()) == null) {
                UserIsolation userIsolation = UserIsolation.builder()
                        .userIdx(user.getId())
                        .loginId(user.getLoginId())
                        .password(user.getPassword())
                        .email(user.getEmail())
                        .lastLogin(user.getLastLogin())
                        .requestedQuitDate(user.getRequestedQuitDate())
                        .build();
                userIsolationCRUDService.saveUserIsolation(userIsolation);
                userCRUDService.softDeleteForIsolation(user.getId());
            }
        }
        log.info("{} - 휴면 계정 전환 종료", LocalDateTime.now());
    }

    @Scheduled(cron = "6 0 0 * * *")
    public void sendEmailAutoDeleteTargeted() {
        log.info("{} - 자동 삭제 이메일 전송 시작", LocalDateTime.now());
        LocalDateTime startTime = LocalDateTime.now().minusMonths(36);
        LocalDateTime endTime = LocalDateTime.now().minusMonths(35);
        List<User> users = userCRUDService.loadUsersLastLoginBetweenStartEnd(startTime, endTime);
        for (User user : users) {
            emailSender.send(user.getEmail(), userAutoDeletedWarningForm.buildEmail());
        }
        log.info("{} - 자동 삭제 이메일 전송 종료", LocalDateTime.now());
    }

    // 3년간 로그인 하지 않으면 계정 자동 삭제
    @Scheduled(cron = "8 0 0 * * *")
    public void autoDeleteTargetIsThreeYears() {
        log.info("{} - 자동 삭제 시작", LocalDateTime.now());
        LocalDateTime startTime = LocalDateTime.now().minusMonths(100);
        LocalDateTime endTime = LocalDateTime.now().minusMonths(36);
        List<User> users = userCRUDService.loadUsersLastLoginBetweenStartEnd(startTime, endTime);
        for (User user : users) {
            Long userIdx = user.getId();
            viewExamCRUDService.deleteAllFromUserIdx(userIdx);
            refreshTokenCRUDService.deleteFromUserIdx(userIdx);
            reportPostService.deleteFromUserIdx(userIdx);
            evaluatePostCRUDService.deleteFromUserIdx(userIdx);
            examPostCRUDService.deleteFromUserIdx(userIdx);
            favoriteMajorService.deleteFromUserIdx(userIdx);
            restrictingUserService.deleteFromUserIdx(userIdx);
            confirmationTokenCRUDService.deleteFromUserIdx(userIdx);
            userIsolationCRUDService.deleteByUserIdx(userIdx);
            userCRUDService.deleteFromUserIdx(userIdx);
        }
        log.info("{} - 자동 삭제 종료", LocalDateTime.now());
    }
}