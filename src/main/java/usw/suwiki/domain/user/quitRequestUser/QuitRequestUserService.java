package usw.suwiki.domain.user.quitRequestUser;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.email.ConfirmationTokenRepository;
import usw.suwiki.domain.email.ConfirmationTokenService;
import usw.suwiki.domain.evaluation.EvaluatePostsService;
import usw.suwiki.domain.exam.ExamPostsRepository;
import usw.suwiki.domain.exam.ExamPostsService;
import usw.suwiki.domain.favorite_major.FavoriteMajorService;
import usw.suwiki.domain.refreshToken.RefreshTokenRepository;
import usw.suwiki.domain.reportTarget.EvaluateReportRepository;
import usw.suwiki.domain.reportTarget.ExamReportRepository;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserRepository;
import usw.suwiki.domain.user.UserService;
import usw.suwiki.domain.user.restrictingUser.RestrictingUserRepository;
import usw.suwiki.domain.userIsolation.UserIsolation;
import usw.suwiki.domain.userIsolation.UserIsolationRepository;
import usw.suwiki.domain.viewExam.ViewExamService;

import java.time.LocalDateTime;
import java.util.List;

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

    //회원탈퇴 요청 유저 일부 데이터 초기화
    @Transactional
    public void disableUser(User user) {
        user.setRestricted(true);
        user.setRestrictedCount(null);
        user.setRole(null);
        user.setWrittenEvaluation(null);
        user.setWrittenExam(null);
        user.setViewExamCount(null);
        user.setPoint(null);
        user.setLastLogin(null);
        user.setCreatedAt(null);
        user.setUpdatedAt(null);
    }

    // 회원탈퇴 요청 시각 스탬프
    @Transactional
    public void requestQuitDateStamp(User user) {
        user.setRequestedQuitDate(LocalDateTime.now());
    }

    //회원탈퇴 대기
    @Transactional
    public void waitQuit(Long userIdx) {

        //구매한 시험 정보 삭제
        viewExamService.deleteByUserIdx(userIdx);

        //회원탈퇴 요청한 유저의 강의평가 삭제
        evaluatePostsService.deleteByUser(userIdx);

        //회원탈퇴 요청한 유저의 시험정보 삭제
        examPostsService.deleteByUser(userIdx);

        //유저 이용불가 처리
        disableUser(userService.loadUserFromUserIdx(userIdx));
    }

    // 회원탈퇴 요청 후 30일 뒤 테이블에서 제거
    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void deleteRequestQuitUserAfter30Days() {

        LocalDateTime targetTime = LocalDateTime.now().minusDays(30);

        List<User> targetUser = userRepository.findByRequestedQuitDateBefore(targetTime);

        List<UserIsolation> targetUserIsolation = userIsolationRepository.findByRequestedQuitDateBefore(targetTime);

        if (targetUser.size() > 0) {
            for (int i = 0; i < targetUser.toArray().length; i++) {

                // 삭제 예정 유저의 구매한 시험 정보 삭제
                viewExamService.deleteByUserIdx(targetUser.get(i).getId());

                // 리프레시 토큰 삭제
                refreshTokenRepository.deleteByUserIdx(targetUserIsolation.get(i).getId());

                // 신고된 시험정보 삭제
                examReportRepository.deleteByReportedUserIdx(targetUser.get(i).getId());
                examReportRepository.deleteByReportingUserIdx(targetUser.get(i).getId());

                // 신고된 강의평가 삭제
                evaluateReportRepository.deleteByReportingUserIdx(targetUser.get(i).getId());
                evaluateReportRepository.deleteByReportedUserIdx(targetUser.get(i).getId());

                // 삭제 예정 유저의 강의평가 삭제
                evaluatePostsService.deleteByUser(targetUser.get(i).getId());

                // 삭제 예정 유저의 시험정보 삭제
                examPostsService.deleteByUser(targetUser.get(i).getId());

                // 즐겨찾기 게시글 삭제
                favoriteMajorService.deleteAllByUser(targetUser.get(i).getId());

                // 제한 테이블에서 삭제
                restrictingUserRepository.deleteByUserIdx(targetUser.get(i).getId());

                // 이메일 인증 토큰 삭제
                confirmationTokenRepository.deleteByUserIdx(targetUser.get(i).getId());

                // 본 테이블에서 유저 삭제
                userRepository.deleteById(targetUser.get(i).getId());
            }
        } else if (targetUser.size() == 0) {
            for (int i = 0; i < targetUserIsolation.toArray().length; i++) {

                // 삭제 예정 유저의 구매한 시험 정보 삭제
                viewExamService.deleteByUserIdx(targetUserIsolation.get(i).getUserIdx());
                
                // 리프레시 토큰 삭제
                refreshTokenRepository.deleteByUserIdx(targetUserIsolation.get(i).getUserIdx());

                // 신고된 시험정보 삭제
                examReportRepository.deleteByReportedUserIdx(targetUserIsolation.get(i).getUserIdx());
                examReportRepository.deleteByReportingUserIdx(targetUserIsolation.get(i).getUserIdx());

                // 신고된 강의평가 삭제
                evaluateReportRepository.deleteByReportingUserIdx(targetUserIsolation.get(i).getUserIdx());
                evaluateReportRepository.deleteByReportedUserIdx(targetUserIsolation.get(i).getUserIdx());

                // 삭제 예정 유저의 강의평가 삭제
                evaluatePostsService.deleteByUser(targetUserIsolation.get(i).getUserIdx());

                // 삭제 예정 유저의 시험정보 삭제
                examPostsService.deleteByUser(targetUserIsolation.get(i).getUserIdx());

                // 즐겨찾기 게시글 삭제
                favoriteMajorService.deleteAllByUser(targetUserIsolation.get(i).getUserIdx());

                // 제한 테이블에서 삭제
                restrictingUserRepository.deleteByUserIdx(targetUserIsolation.get(i).getUserIdx());

                // 이메일 인증 토큰 삭제
                confirmationTokenRepository.deleteByUserIdx(targetUserIsolation.get(i).getUserIdx());

                // 휴면계정에서 유저 삭제
                userIsolationRepository.deleteByLoginId(targetUserIsolation.get(i).getLoginId());
            }
        }

    }
}
