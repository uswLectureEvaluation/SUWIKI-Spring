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
import usw.suwiki.domain.postreport.repository.EvaluateReportRepository;
import usw.suwiki.domain.postreport.repository.ExamReportRepository;
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

    // User
    private final UserService userService;
    private final UserRepository userRepository;
    private final FavoriteMajorService favoriteMajorService;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    // 휴면 계정
    private final UserIsolationRepository userIsolationRepository;
    // 회원탈퇴 요청 계정
    private final ViewExamService viewExamService;
    private final EvaluatePostsService evaluatePostsService;
    private final ExamPostsService examPostsService;
    private final RestrictingUserRepository restrictingUserRepository;
    private final ExamReportRepository examReportRepository;
    private final EvaluateReportRepository evaluateReportRepository;

    // 회원탈퇴 요청 후 30일 뒤 테이블에서 제거
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteRequestQuitUserAfter30Days() {

        LocalDateTime targetTime = LocalDateTime.now().minusDays(30);
        List<User> targetUser = userRepository.findByRequestedQuitDateBefore(targetTime);
        List<UserIsolation> targetUserIsolation = userIsolationRepository.findByRequestedQuitDateBefore(
            targetTime);
        if (targetUser.size() > 0) {
            for (int numberOfTargetUser = 0; numberOfTargetUser < targetUser.toArray().length;
                numberOfTargetUser++) {
                // 삭제 예정 유저의 구매한 시험 정보 삭제
                viewExamService.deleteByUserIdx(targetUser.get(numberOfTargetUser).getId());
                // 리프레시 토큰 삭제
                refreshTokenRepository.deleteByUserIdx(
                    targetUserIsolation.get(numberOfTargetUser).getId());
                // 신고된 시험정보 삭제
                examReportRepository.deleteByReportedUserIdx(
                    targetUser.get(numberOfTargetUser).getId());
                examReportRepository.deleteByReportingUserIdx(
                    targetUser.get(numberOfTargetUser).getId());
                // 신고된 강의평가 삭제
                evaluateReportRepository.deleteByReportingUserIdx(
                    targetUser.get(numberOfTargetUser).getId());
                evaluateReportRepository.deleteByReportedUserIdx(
                    targetUser.get(numberOfTargetUser).getId());
                // 삭제 예정 유저의 강의평가 삭제
                evaluatePostsService.deleteByUser(targetUser.get(numberOfTargetUser).getId());
                // 삭제 예정 유저의 시험정보 삭제
                examPostsService.deleteByUser(targetUser.get(numberOfTargetUser).getId());
                // 즐겨찾기 게시글 삭제
                favoriteMajorService.deleteAllByUser(targetUser.get(numberOfTargetUser).getId());
                // 제한 테이블에서 삭제
                restrictingUserRepository.deleteByUserIdx(
                    targetUser.get(numberOfTargetUser).getId());
                // 이메일 인증 토큰 삭제
                confirmationTokenRepository.deleteByUserIdx(
                    targetUser.get(numberOfTargetUser).getId());
                // 본 테이블에서 유저 삭제
                userRepository.deleteById(targetUser.get(numberOfTargetUser).getId());
            }
        } else if (targetUser.size() == 0) {
            for (int i = 0; i < targetUserIsolation.toArray().length; i++) {
                // 삭제 예정 유저의 구매한 시험 정보 삭제
                viewExamService.deleteByUserIdx(targetUserIsolation.get(i).getUserIdx());
                // 리프레시 토큰 삭제
                refreshTokenRepository.deleteByUserIdx(targetUserIsolation.get(i).getUserIdx());
                // 신고된 시험정보 삭제
                examReportRepository.deleteByReportedUserIdx(
                    targetUserIsolation.get(i).getUserIdx());
                examReportRepository.deleteByReportingUserIdx(
                    targetUserIsolation.get(i).getUserIdx());
                // 신고된 강의평가 삭제
                evaluateReportRepository.deleteByReportingUserIdx(
                    targetUserIsolation.get(i).getUserIdx());
                evaluateReportRepository.deleteByReportedUserIdx(
                    targetUserIsolation.get(i).getUserIdx());
                // 삭제 예정 유저의 강의평가 삭제
                evaluatePostsService.deleteByUser(targetUserIsolation.get(i).getUserIdx());
                // 삭제 예정 유저의 시험정보 삭제
                examPostsService.deleteByUser(targetUserIsolation.get(i).getUserIdx());
                // 즐겨찾기 게시글 삭제
                favoriteMajorService.deleteAllByUser(targetUserIsolation.get(i).getUserIdx());
                // 제한 테이블에서 삭제
                restrictingUserRepository.deleteByUserIdx(targetUserIsolation.get(i).getUserIdx());
                // 이메일 인증 토큰 삭제
                confirmationTokenRepository.deleteByUserIdx(
                    targetUserIsolation.get(i).getUserIdx());
                // 휴면계정에서 유저 삭제
                userIsolationRepository.deleteByLoginId(targetUserIsolation.get(i).getLoginId());
            }
        }

    }
}
