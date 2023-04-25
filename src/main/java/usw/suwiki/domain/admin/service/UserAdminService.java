package usw.suwiki.domain.admin.service;

import static usw.suwiki.global.exception.ExceptionType.SERVER_ERROR;
import static usw.suwiki.global.exception.ExceptionType.USER_ALREADY_BLACKLISTED;
import static usw.suwiki.global.exception.ExceptionType.USER_RESTRICTED;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.blacklistdomain.BlacklistRepository;
import usw.suwiki.domain.blacklistdomain.entity.BlacklistDomain;
import usw.suwiki.domain.evaluation.entity.EvaluatePosts;
import usw.suwiki.domain.evaluation.service.EvaluatePostsService;
import usw.suwiki.domain.exam.entity.ExamPosts;
import usw.suwiki.domain.exam.service.ExamPostsService;
import usw.suwiki.domain.postreport.entity.EvaluatePostReport;
import usw.suwiki.domain.postreport.entity.ExamPostReport;
import usw.suwiki.domain.postreport.repository.EvaluateReportRepository;
import usw.suwiki.domain.postreport.repository.ExamReportRepository;
import usw.suwiki.domain.user.user.dto.UserRequestDto;
import usw.suwiki.domain.user.user.entity.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.domain.user.user.service.UserService;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtProvider;
import usw.suwiki.global.jwt.JwtResolver;
import usw.suwiki.global.jwt.JwtValidator;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAdminService {

    private final Long BANNED_PERIOD = 365L;
    private final Integer NESTED_RESTRICTED_TIME = 3;
    private final UserService userService;
    private final BlacklistRepository blacklistRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EvaluatePostsService evaluatePostsService;
    private final ExamPostsService examPostsService;
    private final EvaluateReportRepository evaluateReportRepository;
    private final ExamReportRepository examReportRepository;
    private final JwtProvider jwtProvider;
    private final JwtValidator jwtValidator;
    private final JwtResolver jwtResolver;
    private final UserRepository userRepository;

    // 관리자 권한 검증
    public void executeValidateAdmin(String authorization) {
        jwtValidator.validateJwt(authorization);
        if (!jwtResolver.getUserRole(authorization).equals("ADMIN")) {
            throw new AccountException(USER_RESTRICTED);
        }
    }

    // 관리자 로그인
    public Map<String, String> adminLogin(UserRequestDto.LoginForm loginForm) {
        if (userService.matchPassword(loginForm.getLoginId(), loginForm.getPassword())) {
            User user = userService.loadUserFromLoginId(loginForm.getLoginId());
            String accessToken = jwtProvider.createAccessToken(user);
            int userCount = userRepository.findAll().size();
            return new HashMap<>() {{
                put("AccessToken", accessToken);
                put("UserCount", String.valueOf(userCount));
            }};
        }
        throw new AccountException(ExceptionType.PASSWORD_ERROR);
    }

    // 블랙리스트 추가
    public void executeBlacklist(
        Long userIdx, Long bannedPeriod, String bannedReason, String judgement
    ) {
        User user = userService.loadUserFromUserIdx(userIdx);
        user.editRestricted(true);

        String hashTargetEmail = bCryptPasswordEncoder.encode(user.getEmail());
        if (blacklistRepository.findByUserId(user.getId()).isPresent()) {
            throw new AccountException(USER_ALREADY_BLACKLISTED);
        }

        if (user.getRestrictedCount() >= NESTED_RESTRICTED_TIME) {
            bannedPeriod += BANNED_PERIOD;
        }

        BlacklistDomain blacklistDomain = BlacklistDomain.builder()
            .userIdx(user.getId())
            .bannedReason(bannedReason)
            .hashedEmail(hashTargetEmail)
            .judgement(judgement)
            .expiredAt(LocalDateTime.now().plusDays(bannedPeriod))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        blacklistRepository.save(blacklistDomain);
    }

    // 강의평가 게시글 제거 및 제거로 인한 게시물 상태 반영
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

    // 시험정보 제거 및 제거로 인한 게시물 상태 반영
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

    public void plusRestrictCount(Long userIdx) {
        User user = userService.loadUserFromUserIdx(userIdx);
        user.increaseRestrictedCountByReportedPost();
    }

    public void plusReportingUserPoint(Long reportingUserIdx) {
        User user = userService.loadUserFromUserIdx(reportingUserIdx);
        user.increasePointByReporting();
    }

    public List<EvaluatePostReport> loadReportedEvaluateList() {
        return evaluateReportRepository.loadAllReportedPosts();
    }

    public List<ExamPostReport> loadReportedExamList() {
        return examReportRepository.loadAllReportedPosts();
    }

    public boolean isAlreadyBlackList(long userIdx) {
        return blacklistRepository.findByUserId(userIdx).isPresent();
    }
}
