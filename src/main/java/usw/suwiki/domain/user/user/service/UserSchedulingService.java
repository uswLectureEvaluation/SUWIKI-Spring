package usw.suwiki.domain.user.user.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.confirmationtoken.repository.ConfirmationTokenRepository;
import usw.suwiki.domain.evaluatepost.service.EvaluatePostCRUDService;
import usw.suwiki.domain.exampost.service.ExamPostCRUDService;
import usw.suwiki.domain.favoritemajor.service.FavoriteMajorService;
import usw.suwiki.domain.postreport.service.ReportPostService;
import usw.suwiki.domain.refreshtoken.repository.RefreshTokenRepository;
import usw.suwiki.domain.restrictinguser.repository.RestrictingUserRepository;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.domain.user.userIsolation.UserIsolation;
import usw.suwiki.domain.user.userIsolation.repository.UserIsolationRepository;
import usw.suwiki.domain.userlecture.viewexam.service.ViewExamCRUDService;
import usw.suwiki.global.util.emailBuild.BuildPersonalInformationUsingNotifyForm;
import usw.suwiki.global.util.mailsender.EmailSendService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserSchedulingService {

    private final UserRepository userRepository;
    private final BuildPersonalInformationUsingNotifyForm buildPersonalInformationUsingNotifyForm;
    private final EmailSendService emailSendService;
    private final FavoriteMajorService favoriteMajorService;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserIsolationRepository userIsolationRepository;
    private final ViewExamCRUDService viewExamCRUDService;
    private final EvaluatePostCRUDService evaluatePostCRUDService;
    private final ExamPostCRUDService examPostCRUDService;
    private final RestrictingUserRepository restrictingUserRepository;
    private final ReportPostService reportPostService;

    @Transactional(readOnly = true)
    @Scheduled(cron = "0 1 0 1 3 *")
    public void sendPrivacyPolicyMail() {
        log.info("{} - 개인정보 처리 방침 안내 발송 시작", LocalDateTime.now());
        List<User> users = userRepository.findAll();
        String emailContent = buildPersonalInformationUsingNotifyForm.buildEmail();
        for (User user : users) {
            emailSendService.send(user.getEmail(), emailContent);
        }
        log.info("{} - 개인정보 처리 방침 안내 발송 종료", LocalDateTime.now());
    }

    @Transactional
    @Scheduled(cron = "0 0 * * * *")
    public void deleteRequestQuitUserAfter30Days() {
        log.info("{} - 회원탈퇴 유저 제거 시작", LocalDateTime.now());
        LocalDateTime targetTime = LocalDateTime.now().minusDays(30);
        List<User> targetUser = userRepository.findByRequestedQuitDateBefore(targetTime);
        List<UserIsolation> targetUserIsolation = userIsolationRepository.findByRequestedQuitDateBefore(targetTime);
        if (targetUser.size() > 0) {
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
        } else if (targetUser.size() == 0) {
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
