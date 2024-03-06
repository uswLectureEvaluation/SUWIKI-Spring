package usw.suwiki.schedule.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.evaluatepost.service.EvaluatePostCRUDService;
import usw.suwiki.domain.exampost.service.ExamPostCRUDService;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserRepository;
import usw.suwiki.domain.user.isolated.UserIsolation;
import usw.suwiki.domain.user.isolated.UserIsolationRepository;
import usw.suwiki.domain.user.major.service.FavoriteMajorService;
import usw.suwiki.domain.user.restricted.RestrictingUserRepository;
import usw.suwiki.domain.user.viewexam.service.ViewExamCRUDService;
import usw.suwiki.external.mail.EmailSender;
import usw.suwiki.report.ReportPostService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserSchedulingService {
    private final EmailSender emailSender;
    private final UserRepository userRepository;
    private final ViewExamCRUDService viewExamCRUDService;
    private final FavoriteMajorService favoriteMajorService;
    private final UserIsolationRepository userIsolationRepository;
    private final RestrictingUserRepository restrictingUserRepository;

    private final RefreshTokenRepository refreshTokenRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    private final ReportPostService reportPostService;
    private final ExamPostCRUDService examPostCRUDService;
    private final EvaluatePostCRUDService evaluatePostCRUDService;

    private final BuildPersonalInformationUsingNotifyForm buildPersonalInformationUsingNotifyForm;

    @Transactional(readOnly = true)
    @Scheduled(cron = "0 1 0 1 3 *")
    public void sendPrivacyPolicyMail() {
        log.info("{} - 개인정보 처리 방침 안내 발송 시작", LocalDateTime.now());

        String emailContent = buildPersonalInformationUsingNotifyForm.buildEmail();
        for (User user : userRepository.findAll()) {
            emailSender.send(user.getEmail(), emailContent);
        }

        log.info("{} - 개인정보 처리 방침 안내 발송 종료", LocalDateTime.now());
    }

    @Scheduled(cron = "0 0 * * * *")
    public void deleteRequestQuitUserAfter30Days() {
        log.info("{} - 회원탈퇴 유저 제거 시작", LocalDateTime.now());

        LocalDateTime targetTime = LocalDateTime.now().minusDays(30);
        List<User> targetUser = userRepository.findByRequestedQuitDateBefore(targetTime);
        List<UserIsolation> targetUserIsolation = userIsolationRepository.findByRequestedQuitDateBefore(targetTime);

        if (!targetUser.isEmpty()) {
            for (int index = 0; index < targetUser.toArray().length; index++) {
                Long userId = targetUser.get(index).getId();
                viewExamCRUDService.deleteAllFromUserIdx(userId);
                refreshTokenRepository.deleteByUserIdx(userId);
                reportPostService.deleteFromUserIdx(userId);
                evaluatePostCRUDService.deleteFromUserIdx(userId);
                examPostCRUDService.deleteFromUserIdx(userId);
                favoriteMajorService.deleteFromUserIdx(userId);
                restrictingUserRepository.deleteByUserIdx(userId);
                confirmationTokenRepository.deleteByUserIdx(userId);
                userRepository.deleteById(userId);
            }
        } else {
            for (int i = 0; i < targetUserIsolation.toArray().length; i++) {
                Long userIdx = targetUserIsolation.get(i).getUserIdx();
                viewExamCRUDService.deleteAllFromUserIdx(userIdx);
                refreshTokenRepository.deleteByUserIdx(userIdx);
                reportPostService.deleteFromUserIdx(userIdx);
                evaluatePostCRUDService.deleteFromUserIdx(userIdx);
                examPostCRUDService.deleteFromUserIdx(userIdx);
                favoriteMajorService.deleteFromUserIdx(userIdx);
                restrictingUserRepository.deleteByUserIdx(userIdx);
                confirmationTokenRepository.deleteByUserIdx(userIdx);
                userIsolationRepository.deleteByLoginId(targetUserIsolation.get(i).getLoginId());
            }
        }

        log.info("{} - 회원탈퇴 유저 제거 종료", LocalDateTime.now());
    }
}
