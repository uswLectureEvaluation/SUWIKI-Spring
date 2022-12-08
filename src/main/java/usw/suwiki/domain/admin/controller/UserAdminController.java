package usw.suwiki.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.*;
import usw.suwiki.domain.admin.dto.UserAdminResponseDto.ViewAllReportedPost;
import usw.suwiki.domain.admin.service.UserAdminService;
import usw.suwiki.domain.blacklistdomain.BlacklistRepository;
import usw.suwiki.domain.postreport.entity.EvaluatePostReport;
import usw.suwiki.domain.postreport.entity.ExamPostReport;
import usw.suwiki.domain.postreport.repository.EvaluateReportRepository;
import usw.suwiki.domain.postreport.repository.ExamReportRepository;
import usw.suwiki.domain.restrictinguser.service.RestrictingUserService;
import usw.suwiki.domain.user.dto.UserDto.LoginForm;
import usw.suwiki.domain.user.entity.User;
import usw.suwiki.domain.user.repository.UserRepository;
import usw.suwiki.domain.user.service.UserService;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtTokenProvider;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.jwt.JwtTokenValidator;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static usw.suwiki.global.exception.ErrorType.SERVER_ERROR;
import static usw.suwiki.global.exception.ErrorType.USER_RESTRICTED;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@CrossOrigin(origins = "https://suwikiman.netlify.app/", allowedHeaders = "*")
public class UserAdminController {

    private final JwtTokenResolver jwtTokenResolver;
    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserAdminService userAdminService;
    private final RestrictingUserService restrictingUserService;
    private final EvaluateReportRepository evaluateReportRepository;
    private final ExamReportRepository examReportRepository;
    private final BlacklistRepository blacklistRepository;

    // 관리자 전용 로그인 API
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> administratorLogin(@Valid @RequestBody LoginForm loginForm) {
        Map<String, String> result = new HashMap<>();
        userService.validatePasswordAtUserTable(loginForm.getLoginId(), loginForm.getPassword());
        User user = userService.loadUserFromLoginId(loginForm.getLoginId());
        String accessToken = jwtTokenProvider.createAccessToken(user);
        result.put("AccessToken", accessToken);
        int userCount = userRepository.findAll().size();
        result.put("UserCount", String.valueOf(userCount));

        return ResponseEntity
                .ok()
                .body(result);
    }

    // 강의평가 게시물 정지 먹이기
    @PostMapping("/restrict/evaluate-post")
    public Map<String, Boolean> restrictEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody EvaluatePostRestrictForm evaluatePostRestrictForm) {

        jwtTokenValidator.validateAccessToken(Authorization);
        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN"))
            throw new AccountException(USER_RESTRICTED);

        restrictingUserService.addRestrictingTableByEvaluatePost(evaluatePostRestrictForm);
        Long reportingUserIdx = userService.whoIsEvaluateReporting(evaluatePostRestrictForm.getEvaluateIdx());
        Long targetUserIdx = userAdminService.banishEvaluatePost(evaluatePostRestrictForm.getEvaluateIdx());
        User targetUser = userAdminService.plusRestrictCount(targetUserIdx);
        User reportingUser = userAdminService.plusReportingUserPoint(reportingUserIdx);
        evaluateReportRepository.deleteByEvaluateIdx(evaluatePostRestrictForm.getEvaluateIdx());

        Map<String, Boolean> result = new HashMap<>();
        result.put("Success", true);
        return result;
    }

    // 시험정보 게시물 정지 먹이기
    @PostMapping("/restrict/exam-post")
    public Map<String, Boolean> restrictExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody ExamPostRestrictForm examPostRestrictForm) {

        jwtTokenValidator.validateAccessToken(Authorization);
        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN"))
            throw new AccountException(USER_RESTRICTED);

        restrictingUserService.addRestrictingTableByExamPost(examPostRestrictForm);
        Long reportingUserIdx = userService.whoIsExamReporting(examPostRestrictForm.getExamIdx());
        Long targetUserIdx = userAdminService.banishExamPost(examPostRestrictForm.getExamIdx());
        User targetUser = userAdminService.plusRestrictCount(targetUserIdx);
        User reportingUser = userAdminService.plusReportingUserPoint(reportingUserIdx);
        examReportRepository.deleteByExamIdx(examPostRestrictForm.getExamIdx());

        Map<String, Boolean> result = new HashMap<>();
        result.put("Success", true);
        return result;
    }


    // 강의평가 게시물 블랙리스트 먹이기
    @PostMapping("/blacklist/evaluate-post")
    public Map<String, Boolean> banEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody EvaluatePostBlacklistForm evaluatePostBlacklistForm) {

        jwtTokenValidator.validateAccessToken(Authorization);
        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN"))
            throw new AccountException(USER_RESTRICTED);

        Long userIdx = userService.loadEvaluatePostsByIndex(evaluatePostBlacklistForm.getEvaluateIdx()).getUser().getId();
        userAdminService.banishEvaluatePost(evaluatePostBlacklistForm.getEvaluateIdx());
        Map<String, Boolean> result = new HashMap<>();

        if (blacklistRepository.findByUserId(userIdx).isPresent()) {
            result.put("이미 블랙리스트에 지정된 사용자 입니다.", false);
            return result;
        }

        userAdminService.banUserByEvaluate(
                userIdx,
                365L,
                evaluatePostBlacklistForm.getBannedReason(),
                evaluatePostBlacklistForm.getJudgement());
        userAdminService.plusRestrictCount(userIdx);

        result.put("Success", true);
        return result;
    }

    // 시험정보 게시물 블랙리스트 먹이기
    @PostMapping("/blacklist/exam-post")
    public Map<String, Boolean> banExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody ExamPostBlacklistForm examPostBlacklistForm) {

        jwtTokenValidator.validateAccessToken(Authorization);

        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN"))
            throw new AccountException(USER_RESTRICTED);

        Long userIdx = userService.loadExamPostsByIndex(examPostBlacklistForm.getExamIdx()).getUser().getId();
        userAdminService.banishExamPost(examPostBlacklistForm.getExamIdx());
        Map<String, Boolean> result = new HashMap<>();

        if (blacklistRepository.findByUserId(userIdx).isPresent()) {
            result.put("이미 블랙리스트에 지정된 사용자 입니다.", false);
            return result;
        }

        userAdminService.banUserByExam(
                userIdx,
                365L,
                examPostBlacklistForm.getBannedReason(),
                examPostBlacklistForm.getJudgement());
        userAdminService.plusRestrictCount(userIdx);

        result.put("Success", true);
        return result;
    }

    // 이상 없는 신고 강의평가 게시글이면 지워주기
    @PostMapping("/no-problem/evaluate-post")
    public Map<String, Boolean> noProblemEv(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody EvaluatePostNoProblemForm evaluatePostNoProblemForm) {

        jwtTokenValidator.validateAccessToken(Authorization);

        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN"))
            throw new AccountException(USER_RESTRICTED);

        evaluateReportRepository.deleteByEvaluateIdx(evaluatePostNoProblemForm.getEvaluateIdx());
        Map<String, Boolean> result = new HashMap<>();
        result.put("Success", true);
        return result;
    }

    // 이상 없는 신고 시험정보 게시글이면 지워주기
    @PostMapping("/no-problem/exam-post")
    public Map<String, Boolean> noProblemEx(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody ExamPostNoProblemForm examPostNoProblemForm) {

        jwtTokenValidator.validateAccessToken(Authorization);

        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN"))
            throw new AccountException(USER_RESTRICTED);

        examReportRepository.deleteByExamIdx(examPostNoProblemForm.getExamIdx());
        Map<String, Boolean> result = new HashMap<>();
        result.put("Success", true);
        return result;
    }

    // 신고받은 게시글 리스트 불러오기
    @GetMapping("/report/list")
    public ResponseEntity<ViewAllReportedPost> loadReportedPost(
            @Valid @RequestHeader String Authorization) {

        jwtTokenValidator.validateAccessToken(Authorization);

        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN"))
            throw new AccountException(USER_RESTRICTED);

        ViewAllReportedPost result = new ViewAllReportedPost();
        result.setEvaluatePostReports(userAdminService.getReportedEvaluateList());
        result.setExamPostReports(userAdminService.getReportedExamList());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    // 강의평가에 관련된 신고 게시글 자세히 보기
    @GetMapping("/report/evaluate/")
    public ResponseEntity<EvaluatePostReport> loadDetailReportedEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestParam Long target) {

        jwtTokenValidator.validateAccessToken(Authorization);

        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN"))
            throw new AccountException(USER_RESTRICTED);

        EvaluatePostReport evaluatePostReport = evaluateReportRepository.findById(target)
                .orElseThrow(() -> new AccountException(SERVER_ERROR));
        return ResponseEntity.status(HttpStatus.OK).body(evaluatePostReport);
    }

    // 시험정보에 관련된 신고 게시글 자세히 보기
    @GetMapping("/report/exam/")
    public ResponseEntity<ExamPostReport> loadDetailReportedExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestParam Long target) {

        jwtTokenValidator.validateAccessToken(Authorization);

        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN"))
            throw new AccountException(USER_RESTRICTED);

        ExamPostReport examPostReport = examReportRepository.findById(target)
                .orElseThrow(() -> new AccountException(SERVER_ERROR));

        return ResponseEntity.status(HttpStatus.OK).body(examPostReport);
    }
}
