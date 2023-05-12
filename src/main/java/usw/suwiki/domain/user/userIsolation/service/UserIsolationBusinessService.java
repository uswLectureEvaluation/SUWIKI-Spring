package usw.suwiki.domain.user.userIsolation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.confirmationtoken.service.ConfirmationTokenCRUDService;
import usw.suwiki.domain.evaluation.service.EvaluatePostCRUDService;
import usw.suwiki.domain.exam.service.ExamPostCRUDService;
import usw.suwiki.domain.exam.service.ViewExamCRUDService;
import usw.suwiki.domain.favoritemajor.service.FavoriteMajorService;
import usw.suwiki.domain.postreport.service.ReportPostService;
import usw.suwiki.domain.refreshToken.service.RefreshTokenCRUDService;
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
public class UserIsolationBusinessService {

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
        LocalDateTime targetTime = LocalDateTime.now().minusMonths(11);
        List<User> users = userCRUDService.loadUsersLastLoginBeforeTargetTime(targetTime);
        for (User user : users) {
            emailSender.send(user.getEmail(), buildSoonDormantTargetForm.buildEmail());
        }
    }

    @Scheduled(cron = "4 0 0 * * *")
    public void convertSleepingTable() {
        LocalDateTime targetTime = LocalDateTime.now().minusMonths(12);
        List<User> users = userCRUDService.loadUsersLastLoginBeforeTargetTime(targetTime);
        for (User user : users) {
            convertToIsolationUser(user);
        }
    }

    @Scheduled(cron = "6 0 0 * * *")
    public void sendEmailAutoDeleteTargeted() {
        LocalDateTime targetTime = LocalDateTime.now().minusYears(3).plusDays(30);
        List<User> users = userCRUDService.loadUsersLastLoginBeforeTargetTime(targetTime);
        for (User user : users) {
            emailSender.send(user.getEmail(), userAutoDeletedWarningForm.buildEmail());
        }
    }

    // 3년간 로그인 하지 않으면 계정 자동 삭제
    @Scheduled(cron = "8 0 0 * * *")
    public void autoDeleteTargetIsThreeYears() {
        LocalDateTime targetTime = LocalDateTime.now().minusYears(3);
        List<UserIsolation> users = userIsolationCRUDService.loadIsolationUsersLastLoginBeforeTargetTime(targetTime);
        for (UserIsolation user : users) {
            Long userIdx = user.getUserIdx();
            viewExamCRUDService.deleteAllFromUserIdx(userIdx);
            refreshTokenCRUDService.deleteFromUserIdx(userIdx);
            reportPostService.deleteFromUserIdx(userIdx);
            evaluatePostCRUDService.deleteFromUserIdx(userIdx);
            examPostCRUDService.deleteFromUserIdx(userIdx);
            favoriteMajorService.deleteFromUserIdx(userIdx);
            restrictingUserService.deleteFromUserIdx(userIdx);
            confirmationTokenCRUDService.deleteFromUserIdx(userIdx);
            userIsolationCRUDService.deleteByUserIdx(user.getUserIdx());
            userCRUDService.deleteFromUserIdx(userIdx);
        }
    }

    private void convertToIsolationUser(User user) {
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