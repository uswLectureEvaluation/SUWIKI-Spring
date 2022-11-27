package usw.suwiki.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.blacklistdomain.BlacklistDomain;
import usw.suwiki.domain.blacklistdomain.BlacklistRepository;
import usw.suwiki.domain.evaluation.entity.EvaluatePosts;
import usw.suwiki.domain.evaluation.service.EvaluatePostsService;
import usw.suwiki.domain.exam.entity.ExamPosts;
import usw.suwiki.domain.exam.service.ExamPostsService;
import usw.suwiki.domain.postreport.entity.EvaluatePostReport;
import usw.suwiki.domain.postreport.entity.ExamPostReport;
import usw.suwiki.domain.postreport.repository.EvaluateReportRepository;
import usw.suwiki.domain.postreport.repository.ExamReportRepository;
import usw.suwiki.domain.user.entity.User;
import usw.suwiki.domain.user.service.UserService;
import usw.suwiki.exception.errortype.AccountException;

import java.time.LocalDateTime;
import java.util.List;

import static usw.suwiki.exception.ErrorType.SERVER_ERROR;
import static usw.suwiki.exception.ErrorType.USER_ALREADY_BLACKLISTED;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAdminService {


    // User 관련 서비스
    private final UserService userService;
    private final BlacklistRepository blacklistRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // Post 관련 서비스
    private final EvaluatePostsService evaluatePostsService;
    private final ExamPostsService examPostsService;

    // Admin 관련 레포지토리
    private final EvaluateReportRepository evaluateReportRepository;
    private final ExamReportRepository examReportRepository;


    // 강의평가 블랙리스트
    public void banUserByEvaluate(Long userIdx, Long bannedPeriod, String bannedReason, String judgement) {
        User user = userService.loadUserFromUserIdx(userIdx);
        user.setRestricted(true);

        //이메일 해싱
        String hashTargetEmail = bCryptPasswordEncoder.encode(user.getEmail());
        if (blacklistRepository.findByUserId(user.getId()).isPresent()) {
            throw new AccountException(USER_ALREADY_BLACKLISTED);
        }

        // 신고 누적 횟수가 3회 이상일 경우
        if (user.getRestrictedCount() >= 3) {
            bannedPeriod += 365L;
        }

        //블랙리스트 도메인 데이터 생성
        BlacklistDomain blacklistDomain = BlacklistDomain.builder()
                .userIdx(user.getId())
                .bannedReason(bannedReason)
                .hashedEmail(hashTargetEmail)
                .judgement(judgement)
                .expiredAt(LocalDateTime.now().plusDays(bannedPeriod))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        //이메일 해싱 값, 유저인덱스 블랙리스트 테이블에 넣기
        blacklistRepository.save(blacklistDomain);
    }

    // 시험정보 블랙리스트
    public void banUserByExam(Long userIdx, Long bannedPeriod, String bannedReason, String judgement) {
        User user = userService.loadUserFromUserIdx(userIdx);
        user.setRestricted(true);

        //이메일 해싱
        String hashTargetEmail = bCryptPasswordEncoder.encode(user.getEmail());

        if (blacklistRepository.findByUserId(user.getId()).isPresent()) {
            throw new AccountException(USER_ALREADY_BLACKLISTED);
        }

        // 신고 누적 횟수가 3회 이상일 경우
        if (user.getRestrictedCount() >= 3) {
            bannedPeriod += 365L;
        }

        //블랙리스트 도메인 데이터 생성
        BlacklistDomain blacklistDomain = BlacklistDomain.builder()
                .userIdx(user.getId())
                .bannedReason(bannedReason)
                .judgement(judgement)
                .hashedEmail(hashTargetEmail)
                .expiredAt(LocalDateTime.now().plusDays(bannedPeriod))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        //이메일 해싱 값, 유저인덱스 블랙리스트 테이블에 넣기
        blacklistRepository.save(blacklistDomain);
    }

    // 신고받은 강의평가 게시글 삭제 해주기
    public Long banishEvaluatePost(Long evaluateIdx) {
        if (userService.loadEvaluatePostsByIndex(evaluateIdx) != null) {
            EvaluatePosts targetedEvaluatePost = userService.loadEvaluatePostsByIndex(evaluateIdx);
            Long targetedEvaluatePostIdx = targetedEvaluatePost.getId();
            Long targetedUserIdx = targetedEvaluatePost.getUser().getId();
            evaluateReportRepository.deleteByEvaluateIdx(targetedEvaluatePostIdx);
            evaluatePostsService.deleteById(targetedEvaluatePostIdx, targetedUserIdx);
            return targetedUserIdx;
        }
        throw new AccountException(SERVER_ERROR);
    }

    // 신고받은 시험정보 게시글 삭제 해주기
    public Long blacklistOrRestrictAndDeleteExamPost(Long examIdx) {
        if (userService.loadExamPostsByIndex(examIdx) != null) {
            ExamPosts targetedExamPost = userService.loadExamPostsByIndex(examIdx);
            Long targetedExamPostIdx = targetedExamPost.getId();
            Long targetedUserIdx = targetedExamPost.getUser().getId();
            examReportRepository.deleteByExamIdx(targetedExamPostIdx);
            examPostsService.deleteById(targetedExamPostIdx, targetedUserIdx);
            return targetedUserIdx;
        }
        throw new AccountException(SERVER_ERROR);
    }

    // 정지 횟수 +1
    public User plusRestrictCount(Long userIdx) {
        User user = userService.loadUserFromUserIdx(userIdx);
        user.setRestrictedCount(user.getRestrictedCount() + 1);
        return user;
    }

    // 신고한 유저 포인트 1 증가
    public User plusReportingUserPoint(Long reportingUserIdx) {
        User user = userService.loadUserFromUserIdx(reportingUserIdx);
        user.setPoint(user.getPoint() + 1);
        return user;
    }

    //신고 받은 강의평가 모두 불러오기
    public List<EvaluatePostReport> getReportedEvaluateList() {
        return evaluateReportRepository.loadAllReportedPosts();
    }

    //신고 받은 시험정보 모두 불러오기
    public List<ExamPostReport> getReportedExamList() {
        return examReportRepository.loadAllReportedPosts();
    }
}
