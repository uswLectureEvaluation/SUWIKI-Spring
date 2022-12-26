package usw.suwiki.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.email.repository.ConfirmationTokenRepository;
import usw.suwiki.domain.evaluation.service.EvaluatePostsService;
import usw.suwiki.domain.exam.service.ExamPostsService;
import usw.suwiki.domain.favoritemajor.service.FavoriteMajorService;
import usw.suwiki.domain.postreport.repository.EvaluateReportRepository;
import usw.suwiki.domain.postreport.repository.ExamReportRepository;
import usw.suwiki.domain.refreshToken.repository.RefreshTokenRepository;
import usw.suwiki.domain.user.entity.User;
import usw.suwiki.domain.user.repository.UserRepository;
import usw.suwiki.domain.user.repository.RestrictingUserRepository;
import usw.suwiki.domain.user.service.UserCommonService;
import usw.suwiki.domain.userIsolation.entity.UserIsolation;
import usw.suwiki.domain.userIsolation.repository.UserIsolationRepository;
import usw.suwiki.domain.viewExam.service.ViewExamService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QuitRequestUserService {

    // User
    private final UserCommonService userCommonService;
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
    // [다시 봐야하는 로직]
    @Transactional
    public void disableUser(User user) {
        User disableTargetUser = User.builder()
                .restricted(true)
                .restrictedCount(null)
                .role(null)
                .writtenEvaluation(null)
                .writtenExam(null)
                .viewExamCount(null)
                .point(null)
                .lastLogin(null)
                .createdAt(null)
                .updatedAt(null)
                .build();
    }

    // 회원탈퇴 요청 시각 스탬프
    @Transactional
    public void requestQuitDateStamp(User user) {
        User disableTargetUser = User.builder()
                .requestedQuitDate(LocalDateTime.now())
                .build();
        userRepository.save(disableTargetUser);
    }

    //회원탈퇴 대기
    @Transactional
    public void waitQuit(Long userIdx) {
        // 즐겨찾는 과목 제거
        favoriteMajorService.deleteAllByUser(userIdx);
        // 구매한 시험 정보 삭제
        viewExamService.deleteByUserIdx(userIdx);
        // 회원탈퇴 요청한 유저의 강의평가 삭제
        evaluatePostsService.deleteByUser(userIdx);
        // 회원탈퇴 요청한 유저의 시험정보 삭제
        examPostsService.deleteByUser(userIdx);
        // 유저 이용불가 처리
        disableUser(userCommonService.loadUserFromUserIdx(userIdx));
    }

    // 회원탈퇴 요청 후 30일 뒤 테이블에서 제거
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteRequestQuitUserAfter30Days() {

        LocalDateTime targetTime = LocalDateTime.now().minusDays(30);
        List<User> targetUser = userRepository.findByRequestedQuitDateBefore(targetTime);
        List<UserIsolation> targetUserIsolation = userIsolationRepository.findByRequestedQuitDateBefore(targetTime);
        if (targetUser.size() > 0) {
            for (int numberOfTargetUser = 0; numberOfTargetUser < targetUser.toArray().length; numberOfTargetUser++) {
                // 삭제 예정 유저의 구매한 시험 정보 삭제
                viewExamService.deleteByUserIdx(targetUser.get(numberOfTargetUser).getId());
                // 리프레시 토큰 삭제
                refreshTokenRepository.deleteByUserIdx(targetUserIsolation.get(numberOfTargetUser).getId());
                // 신고된 시험정보 삭제
                examReportRepository.deleteByReportedUserIdx(targetUser.get(numberOfTargetUser).getId());
                examReportRepository.deleteByReportingUserIdx(targetUser.get(numberOfTargetUser).getId());
                // 신고된 강의평가 삭제
                evaluateReportRepository.deleteByReportingUserIdx(targetUser.get(numberOfTargetUser).getId());
                evaluateReportRepository.deleteByReportedUserIdx(targetUser.get(numberOfTargetUser).getId());
                // 삭제 예정 유저의 강의평가 삭제
                evaluatePostsService.deleteByUser(targetUser.get(numberOfTargetUser).getId());
                // 삭제 예정 유저의 시험정보 삭제
                examPostsService.deleteByUser(targetUser.get(numberOfTargetUser).getId());
                // 즐겨찾기 게시글 삭제
                favoriteMajorService.deleteAllByUser(targetUser.get(numberOfTargetUser).getId());
                // 제한 테이블에서 삭제
                restrictingUserRepository.deleteByUserIdx(targetUser.get(numberOfTargetUser).getId());
                // 이메일 인증 토큰 삭제
                confirmationTokenRepository.deleteByUserIdx(targetUser.get(numberOfTargetUser).getId());
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
