package usw.suwiki.domain.userAdmin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.blacklistDomain.BlacklistDomain;
import usw.suwiki.domain.evaluation.EvaluatePosts;
import usw.suwiki.domain.exam.ExamPosts;
import usw.suwiki.domain.reportTarget.EvaluatePostReport;
import usw.suwiki.domain.reportTarget.ExamPostReport;
import usw.suwiki.domain.user.User;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.domain.blacklistDomain.BlacklistRepository;
import usw.suwiki.domain.reportTarget.EvaluateReportRepository;
import usw.suwiki.domain.reportTarget.ExamReportRepository;
import usw.suwiki.domain.evaluation.EvaluatePostsService;
import usw.suwiki.domain.exam.ExamPostsService;
import usw.suwiki.domain.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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


    // 신고받은 유저 데이터 -> 블랙리스트 테이블로 해싱
    @Transactional
    public void banUserByEvaluate(Long userIdx, Long bannedPeriod, String bannedReason, String judgement) {

        User user = userService.loadUserFromUserIdx(userIdx);

        user.setRestricted(true);

        //이메일 해싱
        String hashTargetEmail = bCryptPasswordEncoder.encode(user.getEmail());

        if (blacklistRepository.findByUserId(user.getId()).isPresent()) {
            throw new AccountException(ErrorType.USER_ALREADY_BLACKLISTED);
        }

        //블랙리스트 도메인 데이터 생성
        BlacklistDomain blacklistDomain = BlacklistDomain.builder()
                .userIdx(user.getId())
                .bannedReason(bannedReason)
                .hashedEmail(hashTargetEmail)
                .build();

        //이메일 해싱 값, 유저인덱스 블랙리스트 테이블에 넣기
        blacklistRepository.save(blacklistDomain);

        //유저 index 로 객체 받아오기
        if (blacklistRepository.findByUserId(user.getId()).isEmpty())
            throw new AccountException(ErrorType.USER_NOT_EXISTS);

        //Optional 객체 받아오기
        Optional<BlacklistDomain> expiredAtSetTarget = blacklistRepository.findByUserId(user.getId());

        // 신고 누적 횟수가 3회 이상일 경우
        if (user.getRestrictedCount() >= 3) {
            bannedPeriod += 365L;
        }

        //index 로 받온 객체에 제한 시간 걸기
        expiredAtSetTarget.get().setExpiredAt(LocalDateTime.now().plusDays(bannedPeriod));
        expiredAtSetTarget.get().setCreatedAt(LocalDateTime.now());
        expiredAtSetTarget.get().setCreatedAt(LocalDateTime.now());
    }

    // 신고받은 강의평가 작성자 유저 정지먹이기 (블랙리스트가 아님)
    @Transactional
    public void restrictUserByEvaluate(Long userIdx, Long bannedPeriod, String bannedReason, String judgement) {

        User user = userService.loadUserFromUserIdx(userIdx);

        user.setRestricted(true);
        user.setRestrictedCount(user.getRestrictedCount()+1);

    }

    // 신고받은 시험정보 작성자 유저 정지먹이기 (블랙리스트가 아님)
    @Transactional
    public void restrictUserByExam(Long userIdx, Long bannedPeriod, String bannedReason, String judgement) {

        User user = userService.loadUserFromUserIdx(userIdx);

        user.setRestricted(true);

        //이메일 해싱
        String hashTargetEmail = bCryptPasswordEncoder.encode(user.getEmail());

        if (blacklistRepository.findByUserId(user.getId()).isPresent()) {
            throw new AccountException(ErrorType.USER_ALREADY_BLACKLISTED);
        }

        //블랙리스트 도메인 데이터 생성
        BlacklistDomain blacklistDomain = BlacklistDomain.builder()
                .userIdx(user.getId())
                .bannedReason(bannedReason)
                .judgement(judgement)
                .hashedEmail(hashTargetEmail)
                .build();

        //이메일 해싱 값, 유저인덱스 블랙리스트 테이블에 넣기
        blacklistRepository.save(blacklistDomain);

        //유저 index 로 객체 받아오기
        if (blacklistRepository.findByUserId(user.getId()).isEmpty())
            throw new AccountException(ErrorType.USER_NOT_EXISTS);

        //Optional 객체 받아오기
        Optional<BlacklistDomain> expiredAtSetTarget = blacklistRepository.findByUserId(user.getId());

        // 신고 누적 횟수가 3회 이상일 경우
        if (user.getRestrictedCount() >= 3) {
            bannedPeriod += 365L;
        }

        //index 로 받온 객체에 제한 시간 걸기
        expiredAtSetTarget.get().setExpiredAt(LocalDateTime.now().plusDays(bannedPeriod));
        expiredAtSetTarget.get().setCreatedAt(LocalDateTime.now());
        expiredAtSetTarget.get().setCreatedAt(LocalDateTime.now());
    }

    // 신고받은 유저 데이터 -> 블랙리스트 테이블로 해싱
    @Transactional
    public void banUserByExam(Long userIdx, Long bannedPeriod, String bannedReason, String judgement) {

        User user = userService.loadUserFromUserIdx(userIdx);

        user.setRestricted(true);

        //이메일 해싱
        String hashTargetEmail = bCryptPasswordEncoder.encode(user.getEmail());

        if (blacklistRepository.findByUserId(user.getId()).isPresent()) {
            throw new AccountException(ErrorType.USER_ALREADY_BLACKLISTED);
        }

        //블랙리스트 도메인 데이터 생성
        BlacklistDomain blacklistDomain = BlacklistDomain.builder()
                .userIdx(user.getId())
                .bannedReason(bannedReason)
                .judgement(judgement)
                .hashedEmail(hashTargetEmail)
                .build();

        //이메일 해싱 값, 유저인덱스 블랙리스트 테이블에 넣기
        blacklistRepository.save(blacklistDomain);

        //유저 index 로 객체 받아오기
        if (blacklistRepository.findByUserId(user.getId()).isEmpty())
            throw new AccountException(ErrorType.USER_NOT_EXISTS);

        //Optional 객체 받아오기
        Optional<BlacklistDomain> expiredAtSetTarget = blacklistRepository.findByUserId(user.getId());

//        // 신고 누적 횟수가 3회 이상일 경우
//        if (user.getBannedCount() >= 3) {
//            bannedPeriod += 365L;
//        }

        //index 로 받온 객체에 제한 시간 걸기
        expiredAtSetTarget.get().setExpiredAt(LocalDateTime.now().plusDays(bannedPeriod));
        expiredAtSetTarget.get().setCreatedAt(LocalDateTime.now());
        expiredAtSetTarget.get().setUpdatedAt(LocalDateTime.now());

    }

    // 신고받은 강의평가 게시글 삭제 해주기
    @Transactional
    public Long banishEvaluatePost(Long evaluateIdx) {

        if (userService.loadEvaluatePostsByIndex(evaluateIdx) != null) {

            // 추방할 게시글 불러오기
            EvaluatePosts targetedEvaluatePost = userService.loadEvaluatePostsByIndex(evaluateIdx);

            // 게시글 인덱스 불러오기
            Long targetedEvaluatePostIdx = targetedEvaluatePost.getId();

            // 강의평가를 작성한 작성자 인덱스 불러오기
            Long targetedUserIdx = targetedEvaluatePost.getUser().getId();

            // 게시글 인덱스로 신고 테이블에서 지우기(벤 하면 신고 테이블에서도 지워줘야함)
            evaluateReportRepository.deleteByEvaluateIdx(targetedEvaluatePostIdx);

            // 강의 평가 삭제(작성한 게시글 갯수 감소, 포인트 감소 까지 반영)
            evaluatePostsService.deleteById(targetedEvaluatePostIdx, targetedUserIdx);

            return targetedUserIdx;
        }

        throw new AccountException(ErrorType.SERVER_ERROR);
    }

    //신고받은 시험정보 게시글 삭제 해주기
    @Transactional
    public Long banishExamPost(Long examIdx) {

        if (userService.loadExamPostsByIndex(examIdx) != null) {
            // 추방할 게시글 불러오기
            ExamPosts targetedExamPost = userService.loadExamPostsByIndex(examIdx);

            // 게시글 인덱스 불러오기
            Long targetedExamPostIdx = targetedExamPost.getId();

            // 시험정보를 작성한 작성자 인덱스 불러오기
            Long targetedUserIdx = targetedExamPost.getUser().getId();

            // 게시글 인덱스로 신고 테이블에서 지우기(벤 하면 신고 테이블에서도 지워줘야함)
            examReportRepository.deleteByExamIdx(targetedExamPostIdx);

            // 시험 정보 삭제(작성한 게시글 갯수 감소, 포인트 감소 까지 반영)
            examPostsService.deleteById(targetedExamPostIdx, targetedUserIdx);

            return targetedUserIdx;
        }

        throw new AccountException(ErrorType.SERVER_ERROR);
    }

    // 정지 횟수 +1
    @Transactional
    public void plusRestrictCount(Long userIdx) {
        User user = userService.loadUserFromUserIdx(userIdx);
        user.setRestrictedCount(user.getRestrictedCount() + 1);
    }

    // 신고한 유저 포인트 1 증가
    @Transactional
    public void plusReportingUserPoint(Long reportingUserIdx) {
        User user = userService.loadUserFromUserIdx(reportingUserIdx);
        user.setPoint(user.getPoint() + 1);
    }

    //신고 받은 강의평가 모두 불러오기
    @Transactional
    public List<EvaluatePostReport> getReportedEvaluateList() {
        return evaluateReportRepository.loadAllReportedPosts();
    } 
    
    //신고 받은 시험정보 모두 불러오기
    @Transactional
    public List<ExamPostReport> getReportedExamList() {
        return examReportRepository.loadAllReportedPosts();
    }
}
