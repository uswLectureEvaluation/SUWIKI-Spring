package usw.suwiki.schedule.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.auth.token.ConfirmationTokenRepository;
import usw.suwiki.auth.token.RefreshTokenRepository;
import usw.suwiki.core.mail.EmailSender;
import usw.suwiki.domain.evaluatepost.service.EvaluatePostService;
import usw.suwiki.domain.exampost.service.ExamPostCRUDService;
import usw.suwiki.domain.report.service.ReportService;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserRepository;
import usw.suwiki.domain.user.service.ClearViewExamService;
import usw.suwiki.domain.user.service.FavoriteMajorService;
import usw.suwiki.domain.user.service.RestrictingUserService;
import usw.suwiki.domain.user.service.UserIsolationCRUDService;

import java.time.LocalDateTime;
import java.util.List;

import static usw.suwiki.core.mail.MailType.PRIVACY_POLICY_NOTIFICATION;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserSchedulingService {
  private final EmailSender emailSender;
  private final UserRepository userRepository;
  private final ClearViewExamService clearViewExamService;
  private final FavoriteMajorService favoriteMajorService;
  private final RestrictingUserService restrictingUserService;
  private final UserIsolationCRUDService userIsolationCRUDService;

  private final RefreshTokenRepository refreshTokenRepository;
  private final ConfirmationTokenRepository confirmationTokenRepository;

  private final ReportService reportService;
  private final ExamPostCRUDService examPostCRUDService;
  private final EvaluatePostService evaluatePostService;

  @Transactional(readOnly = true)
  @Scheduled(cron = "0 1 0 1 3 *")
  public void sendPrivacyPolicyMail() {
    log.info("{} - 개인정보 처리 방침 안내 발송 시작", LocalDateTime.now());

    userRepository.findAll()
      .forEach(user -> emailSender.send(user.getEmail(), PRIVACY_POLICY_NOTIFICATION));

    log.info("{} - 개인정보 처리 방침 안내 발송 종료", LocalDateTime.now());
  }

  @Scheduled(cron = "0 0 * * * *")
  public void deleteRequestQuitUserAfter30Days() {
    log.info("{} - 회원탈퇴 유저 제거 시작", LocalDateTime.now());

    LocalDateTime targetTime = LocalDateTime.now().minusDays(30);
    List<User> targetUser = userRepository.findByRequestedQuitDateBefore(targetTime);
    List<Long> isolatedUserIds = userIsolationCRUDService.loadAllIsolatedUntilTarget(targetTime);

    if (!targetUser.isEmpty()) {
      for (User user : targetUser) {
        Long userId = user.getId();
        clearViewExamService.clear(userId);
        refreshTokenRepository.deleteByUserIdx(userId);
        reportService.deleteFromUserIdx(userId);
        evaluatePostService.deleteAllByUserId(userId);
        examPostCRUDService.deleteFromUserIdx(userId);
        favoriteMajorService.clear(userId);
        restrictingUserService.releaseByUserId(userId);
        confirmationTokenRepository.deleteByUserIdx(userId);
        userRepository.deleteById(userId);
      }
    } else {
      for (Long isolatedUserId : isolatedUserIds) {
        clearViewExamService.clear(isolatedUserId);
        refreshTokenRepository.deleteByUserIdx(isolatedUserId);
        reportService.deleteFromUserIdx(isolatedUserId);
        evaluatePostService.deleteAllByUserId(isolatedUserId);
        examPostCRUDService.deleteFromUserIdx(isolatedUserId);
        favoriteMajorService.clear(isolatedUserId);
        restrictingUserService.releaseByUserId(isolatedUserId);
        confirmationTokenRepository.deleteByUserIdx(isolatedUserId);
        userIsolationCRUDService.deleteByUserIdx(isolatedUserId);
      }
    }

    log.info("{} - 회원탈퇴 유저 제거 종료", LocalDateTime.now());
  }
}
