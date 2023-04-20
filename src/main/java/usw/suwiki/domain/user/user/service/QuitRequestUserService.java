package usw.suwiki.domain.user.user.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.confirmationtoken.repository.ConfirmationTokenRepository;
import usw.suwiki.domain.evaluation.service.EvaluatePostsService;
import usw.suwiki.domain.exam.service.ExamPostsService;
import usw.suwiki.domain.favoritemajor.service.FavoriteMajorService;
import usw.suwiki.domain.postreport.service.PostReportService;
import usw.suwiki.domain.refreshToken.repository.RefreshTokenRepository;
import usw.suwiki.domain.user.user.entity.User;
import usw.suwiki.domain.user.user.repository.RestrictingUserRepository;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.domain.user.userIsolation.entity.UserIsolation;
import usw.suwiki.domain.user.userIsolation.repository.UserIsolationRepository;
import usw.suwiki.domain.viewExam.service.ViewExamService;

@Service
@RequiredArgsConstructor
@Transactional
public class QuitRequestUserService {

    private final UserRepository userRepository;
    private final FavoriteMajorService favoriteMajorService;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserIsolationRepository userIsolationRepository;
    private final ViewExamService viewExamService;
    private final EvaluatePostsService evaluatePostsService;
    private final ExamPostsService examPostsService;
    private final RestrictingUserRepository restrictingUserRepository;
    private final PostReportService postReportService;

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
                viewExamService.deleteByUserIdx(userId);
                // 리프레시 토큰 삭제
                refreshTokenRepository.deleteByUserIdx(userId);
                // 신고된 게시글 삭제
                postReportService.deleteByUserIdx(userId);
                // 삭제 예정 유저의 강의평가 삭제
                evaluatePostsService.deleteByUser(userId);
                // 삭제 예정 유저의 시험정보 삭제
                examPostsService.deleteByUser(userId);
                // 즐겨찾기 게시글 삭제
                favoriteMajorService.deleteAllByUser(userId);
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
                viewExamService.deleteByUserIdx(userIdx);
                // 리프레시 토큰 삭제
                refreshTokenRepository.deleteByUserIdx(userIdx);
                postReportService.deleteByUserIdx(userIdx);
                // 삭제 예정 유저의 강의평가 삭제
                evaluatePostsService.deleteByUser(userIdx);
                // 삭제 예정 유저의 시험정보 삭제
                examPostsService.deleteByUser(userIdx);
                // 즐겨찾기 게시글 삭제
                favoriteMajorService.deleteAllByUser(userIdx);
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
