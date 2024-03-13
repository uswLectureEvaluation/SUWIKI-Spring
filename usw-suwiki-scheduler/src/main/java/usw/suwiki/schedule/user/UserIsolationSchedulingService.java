package usw.suwiki.schedule.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.auth.token.service.ConfirmationTokenCRUDService;
import usw.suwiki.auth.token.service.RefreshTokenCRUDService;
import usw.suwiki.core.mail.EmailSender;
import usw.suwiki.domain.evaluatepost.service.EvaluatePostCRUDService;
import usw.suwiki.domain.exampost.service.ExamPostCRUDService;
import usw.suwiki.domain.lecture.major.service.FavoriteMajorService;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.service.RestrictingUserService;
import usw.suwiki.domain.user.service.UserCRUDService;
import usw.suwiki.domain.user.service.UserIsolationCRUDService;
import usw.suwiki.domain.user.service.ViewExamCRUDService;
import usw.suwiki.report.ReportPostService;

import java.time.LocalDateTime;

import static usw.suwiki.core.mail.MailType.DELETE_WARNING;
import static usw.suwiki.core.mail.MailType.DORMANT_NOTIFICATION;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserIsolationSchedulingService {
    private final EmailSender emailSender;

    private final UserCRUDService userCRUDService;
    private final RestrictingUserService restrictingUserService;
    private final UserIsolationCRUDService userIsolationCRUDService;
    private final ViewExamCRUDService viewExamCRUDService;
    private final FavoriteMajorService favoriteMajorService;

    private final ReportPostService reportPostService;

    private final EvaluatePostCRUDService evaluatePostCRUDService;
    private final ExamPostCRUDService examPostCRUDService;

    private final RefreshTokenCRUDService refreshTokenCRUDService;
    private final ConfirmationTokenCRUDService confirmationTokenCRUDService;

    @Scheduled(cron = "2 0 0 * * *")
    public void sendEmailAboutSleeping() {
        log.info("{} - 휴면 계정 대상들에게 이메일 전송 시작", LocalDateTime.now());

        LocalDateTime startTime = LocalDateTime.now().minusMonths(11).minusDays(1);
        LocalDateTime endTime = LocalDateTime.now().minusMonths(11);

        for (User user : userCRUDService.loadUsersLastLoginBetweenStartEnd(startTime, endTime)) {
            emailSender.send(user.getEmail(), DORMANT_NOTIFICATION);
        }

        log.info("{} - 휴면 계정 대상들에게 이메일 전송 종료", LocalDateTime.now());
    }

    @Scheduled(cron = "4 0 0 * * *")
    public void convertSleepingTable() {
        log.info("{} - 휴면 계정 전환 시작", LocalDateTime.now());

        LocalDateTime startTime = LocalDateTime.now().minusMonths(35);
        LocalDateTime endTime = LocalDateTime.now().minusMonths(12);

        for (User user : userCRUDService.loadUsersLastLoginBetweenStartEnd(startTime, endTime)) {
            if (userIsolationCRUDService.isNotIsolated(user.getId())) {
                userIsolationCRUDService.saveUserIsolation(user);
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

        for (User user : userCRUDService.loadUsersLastLoginBetweenStartEnd(startTime, endTime)) {
            emailSender.send(user.getEmail(), DELETE_WARNING);
        }

        log.info("{} - 자동 삭제 이메일 전송 종료", LocalDateTime.now());
    }

    @Scheduled(cron = "8 0 0 * * *")
    public void autoDeleteTargetIsThreeYears() {
        log.info("{} - 자동 삭제 시작", LocalDateTime.now());

        LocalDateTime startTime = LocalDateTime.now().minusMonths(100);
        LocalDateTime endTime = LocalDateTime.now().minusMonths(36);

        for (User user : userCRUDService.loadUsersLastLoginBetweenStartEnd(startTime, endTime)) {
            Long userIdx = user.getId();
            viewExamCRUDService.deleteAllFromUserIdx(userIdx);
            refreshTokenCRUDService.deleteFromUserIdx(userIdx);
            reportPostService.deleteFromUserIdx(userIdx);
            evaluatePostCRUDService.deleteFromUserIdx(userIdx);
            examPostCRUDService.deleteFromUserIdx(userIdx);
            favoriteMajorService.deleteFromUserIdx(userIdx);
            restrictingUserService.releaseByUserId(userIdx);
            confirmationTokenCRUDService.deleteFromUserIdx(userIdx);
            userIsolationCRUDService.deleteByUserIdx(userIdx);
            userCRUDService.deleteFromUserIdx(userIdx);
        }

        log.info("{} - 자동 삭제 종료", LocalDateTime.now());
    }
}
