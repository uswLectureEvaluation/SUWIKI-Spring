package usw.suwiki.domain.user.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.restrictinguser.repository.RestrictingUserRepository;
import usw.suwiki.domain.confirmationtoken.repository.ConfirmationTokenRepository;
import usw.suwiki.domain.evaluation.service.EvaluatePostsService;
import usw.suwiki.domain.exam.service.ExamPostsService;
import usw.suwiki.domain.favoritemajor.service.FavoriteMajorService;
import usw.suwiki.domain.postreport.service.ReportPostService;
import usw.suwiki.domain.refreshToken.repository.RefreshTokenRepository;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.domain.user.userIsolation.UserIsolation;
import usw.suwiki.domain.user.userIsolation.repository.UserIsolationRepository;
import usw.suwiki.domain.viewExam.service.ViewExamService;
import usw.suwiki.global.mailsender.EmailSendService;
import usw.suwiki.global.util.emailBuild.BuildPersonalInformationUsingNotifyForm;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserSchedulingService {

    private final UserRepository userRepository;
    private final BuildPersonalInformationUsingNotifyForm buildPersonalInformationUsingNotifyForm;
    private final EmailSendService emailSendService;
    private final FavoriteMajorService favoriteMajorService;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserIsolationRepository userIsolationRepository;
    private final ViewExamService viewExamService;
    private final EvaluatePostsService evaluatePostsService;
    private final ExamPostsService examPostsService;
    private final RestrictingUserRepository restrictingUserRepository;
    private final ReportPostService reportPostService;

    @Transactional(readOnly = true)
    @Scheduled(cron = "0 1 0 1 3 *")
    public void sendPrivacyPolicyMail() {
        List<User> users = userRepository.findAll();
        String emailContent = buildPersonalInformationUsingNotifyForm.buildEmail();
        for (User user : users) {
            emailSendService.send(user.getEmail(), emailContent);
        }
    }

    // 회원탈퇴 요청 후 30일 뒤 테이블에서 제거
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteRequestQuitUserAfter30Days() {
        LocalDateTime targetTime = LocalDateTime.now().minusDays(30);
        List<User> targetUser = userRepository.findByRequestedQuitDateBefore(targetTime);
        List<UserIsolation> targetUserIsolation =
                userIsolationRepository.findByRequestedQuitDateBefore(targetTime);
        if (targetUser.size() > 0) {
            for (int index = 0; index < targetUser.toArray().length; index++) {
                Long userId = targetUser.get(index).getId();
                // 삭제 예정 유저의 구매한 시험 정보 삭제
                viewExamService.deleteFromUserIdx(userId);
                // 리프레시 토큰 삭제
                refreshTokenRepository.deleteByUserIdx(userId);
                // 신고된 게시글 삭제
                reportPostService.deleteFromUserIdx(userId);
                // 삭제 예정 유저의 강의평가 삭제
                evaluatePostsService.deleteFromUserIdx(userId);
                // 삭제 예정 유저의 시험정보 삭제
                examPostsService.deleteFromUserIdx(userId);
                // 즐겨찾기 게시글 삭제
                favoriteMajorService.deleteFromUserIdx(userId);
                // 제한 테이블에서 삭제
                restrictingUserRepository.deleteByUserIdx(userId);
                // 이메일 인증 토큰 삭제
                confirmationTokenRepository.deleteByUserIdx(userId);
                // 본 테이블에서 유저 삭제
                userRepository.deleteById(userId);
            }
        } else if (targetUser.size() == 0) {
            for (int i = 0; i < targetUserIsolation.toArray().length; i++) {
                Long userIdx = targetUserIsolation.get(i).getUserIdx();
                // 삭제 예정 유저의 구매한 시험 정보 삭제
                viewExamService.deleteFromUserIdx(userIdx);
                // 리프레시 토큰 삭제
                refreshTokenRepository.deleteByUserIdx(userIdx);
                reportPostService.deleteFromUserIdx(userIdx);
                // 삭제 예정 유저의 강의평가 삭제
                evaluatePostsService.deleteFromUserIdx(userIdx);
                // 삭제 예정 유저의 시험정보 삭제
                examPostsService.deleteFromUserIdx(userIdx);
                // 즐겨찾기 게시글 삭제
                favoriteMajorService.deleteFromUserIdx(userIdx);
                // 제한 테이블에서 삭제
                restrictingUserRepository.deleteByUserIdx(userIdx);
                // 이메일 인증 토큰 삭제
                confirmationTokenRepository.deleteByUserIdx(userIdx);
                // 휴면계정에서 유저 삭제
                userIsolationRepository.deleteByLoginId(targetUserIsolation.get(i).getLoginId());
            }
        }
    }
}
