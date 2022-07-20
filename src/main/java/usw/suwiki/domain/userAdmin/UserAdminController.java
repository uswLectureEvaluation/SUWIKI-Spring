package usw.suwiki.domain.userAdmin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.blacklistDomain.BlacklistRepository;
import usw.suwiki.domain.reportTarget.EvaluatePostReport;
import usw.suwiki.domain.reportTarget.ExamPostReport;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserDto;
import usw.suwiki.domain.user.restrictingUser.RestrictingUserService;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.global.jwt.JwtTokenProvider;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.jwt.JwtTokenValidator;
import usw.suwiki.domain.reportTarget.EvaluateReportRepository;
import usw.suwiki.domain.reportTarget.ExamReportRepository;
import usw.suwiki.domain.user.UserService;

import javax.validation.Valid;
import java.util.HashMap;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@CrossOrigin(origins = "https://suwikiman.netlify.app/", allowedHeaders = "*")
public class UserAdminController {

    // JWT 관련 의존성
    private final JwtTokenResolver jwtTokenResolver;
    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenProvider jwtTokenProvider;

    // 유저 관련 비즈니스 로직
    private final UserService userService;

    // 관리자 계정 관련 비즈니스 로직
    private final UserAdminService userAdminService;
    private final RestrictingUserService restrictingUserService;

    private final EvaluateReportRepository evaluateReportRepository;
    private final ExamReportRepository examReportRepository;

    private final BlacklistRepository blacklistRepository;

    // 관리자 전용 로그인 API
    @PostMapping("/login")
    public ResponseEntity<HashMap<String, String>> administratorLogin(@Valid @RequestBody UserDto.LoginForm loginForm) {

        HashMap<String, String> token = new HashMap<>();

        //아이디 비밀번호 검증
        userService.validatePasswordAtUserTable(loginForm.getLoginId(), loginForm.getPassword());

        //유저 객체 생성
        User user = userService.loadUserFromLoginId(loginForm.getLoginId());

        //액세스 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user);
        token.put("AccessToken", accessToken);

        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    // 강의평가 게시물 정지 먹이기
    @PostMapping("/restrict/evaluate-post")
    public HashMap<String, Boolean> restrictEvaluatePost(@Valid @RequestHeader String Authorization,
                                                    @Valid @RequestBody UserAdminRequestDto.EvaluatePostRestrictForm evaluatePostRestrictForm) {

        // 토큰 검증
        jwtTokenValidator.validateAccessToken(Authorization);

        // 토큰으로 유저 권한 확인 -> ADMIN 이 아니면 에러
        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN"))
            throw new AccountException(ErrorType.USER_RESTRICTED);

        HashMap<String, Boolean> result = new HashMap<>();

        // 유저 정지 테이블에 값 추가
        restrictingUserService.addRestrictingTableByEvaluatePost(evaluatePostRestrictForm);

        // 신고한 유저 인덱스 가져오기
        Long reportingUserIdx = userService.whoIsEvaluateReporting(evaluatePostRestrictForm.getEvaluateIdx());

        // 게시글 삭제 후 해당 게시글 작성자 인덱스 받아오기
        Long targetUserIdx = userAdminService.banishEvaluatePost(evaluatePostRestrictForm.getEvaluateIdx());

        System.out.println("test1");

        // 유저 restricted True, 정지 카운트 증가
        userAdminService.plusRestrictCount(targetUserIdx);

        // 신고한 유저 포인트 증가
        userAdminService.plusReportingUserPoint(reportingUserIdx);

        result.put("Success", true);
        return result;
    }

    // 시험정보 게시물 정지 먹이기
    @PostMapping("/restrict/exam-post")
    public HashMap<String, Boolean> restrictExamPost(@Valid @RequestHeader String Authorization,
                                                         @Valid @RequestBody UserAdminRequestDto.ExamPostRestrictForm examPostRestrictForm) {

        HashMap<String, Boolean> result = new HashMap<>();

        //토큰 검증
        jwtTokenValidator.validateAccessToken(Authorization);

        //토큰으로 유저 권한 확인 -> ADMIN 이 아니면 에러
        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN"))
            throw new AccountException(ErrorType.USER_RESTRICTED);

        // 유저 정지테이블에 값 추가
        restrictingUserService.addRestrictingTableByExamPost(examPostRestrictForm);

        // 신고한 유저 인덱스 가져오기
        Long reportingUserIdx = userService.whoIsExamReporting(examPostRestrictForm.getExamIdx());

        // 게시글 삭제 후 해당 게시글 작성자 인덱스 받아오기
        Long targetUserIdx = userAdminService.banishExamPost(examPostRestrictForm.getExamIdx());

        // 유저 restricted True, 정지 카운트 증가
        userAdminService.plusRestrictCount(targetUserIdx);

        // 신고한 유저 포인트 증가
        userAdminService.plusReportingUserPoint(reportingUserIdx);

        result.put("Success", true);
        return result;
    }

    
    // 강의평가 게시물 블랙리스트 먹이기
    @PostMapping("/blacklist/evaluate-post")
    public HashMap<String, Boolean> banEvaluatePost(@Valid @RequestHeader String Authorization,
                                                    @Valid @RequestBody UserAdminRequestDto.EvaluatePostBlacklistForm evaluatePostBlacklistForm) {

        // 토큰 검증
        jwtTokenValidator.validateAccessToken(Authorization);

        // 토큰으로 유저 권한 확인 -> ADMIN 이 아니면 에러
        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN"))
            throw new AccountException(ErrorType.USER_RESTRICTED);

        HashMap<String, Boolean> result = new HashMap<>();

        // 게시글 인덱스
        Long evaluateIdx = userAdminService.banishEvaluatePost(evaluatePostBlacklistForm.getEvaluateIdx());
        Long userIdx = userService.loadEvaluatePostsByIndex(evaluateIdx).getUser().getId();


        // 이미 블랙리스트 사용자 일 경우
        if (blacklistRepository.findByUserId(userService.loadEvaluatePostsByIndex(evaluateIdx).getUser().getId()).isPresent()) {
            result.put("이미 블랙리스트에 지정된 사용자 입니다.", false);
            return result;
        }

        // 유저 블랙리스트 테이블로
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
    public HashMap<String, Boolean> banExamPost(@Valid @RequestHeader String Authorization,
                                              @Valid @RequestBody UserAdminRequestDto.ExamPostBlacklistForm examPostBlacklistForm) {

        //토큰 검증
        jwtTokenValidator.validateAccessToken(Authorization);

        //토큰으로 유저 권한 확인 -> ADMIN 이 아니면 에러
        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN"))
            throw new AccountException(ErrorType.USER_RESTRICTED);

        HashMap<String, Boolean> result = new HashMap<>();

        // 게시글 인덱스
        Long examIdx = userAdminService.banishExamPost(examPostBlacklistForm.getExamIdx());
        Long userIdx = userService.loadExamPostsByIndex(examIdx).getUser().getId();

        // 이미 블랙리스트 사용자 일 경우
        if (blacklistRepository.findByUserId(userIdx).isPresent()) {
            result.put("이미 블랙리스트에 지정된 사용자 입니다.", false);
            return result;
        }

        // 유저 블랙리스트 테이블로
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
    public HashMap<String, Boolean> noProblemEv(@Valid @RequestHeader String Authorization,
                                                         @Valid @RequestBody UserAdminRequestDto.EvaluatePostNoProblemForm evaluatePostNoProblemForm) {

        HashMap<String, Boolean> result = new HashMap<>();

        //토큰 검증
        jwtTokenValidator.validateAccessToken(Authorization);

        //토큰으로 유저 권한 확인 -> ADMIN 이 아니면 에러
        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN"))
            throw new AccountException(ErrorType.USER_RESTRICTED);

        evaluateReportRepository.deleteByEvaluateIdx(evaluatePostNoProblemForm.getEvaluateIdx());

        result.put("Success", true);
        return result;
    }

    // 이상 없는 신고 시험정보 게시글이면 지워주기
    @PostMapping("/no-problem/exam-post")
    public HashMap<String, Boolean> noProblemEx(@Valid @RequestHeader String Authorization,
                                              @Valid @RequestBody UserAdminRequestDto.ExamPostNoProblemForm examPostNoProblemForm) {

        HashMap<String, Boolean> result = new HashMap<>();

        //토큰 검증
        jwtTokenValidator.validateAccessToken(Authorization);

        //토큰으로 유저 권한 확인 -> ADMIN 이 아니면 에러
        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN"))
            throw new AccountException(ErrorType.USER_RESTRICTED);

        examReportRepository.deleteByExamIdx(examPostNoProblemForm.getExamIdx());

        result.put("Success", true);
        return result;
    }


    // 신고받은 게시글 리스트 불러오기
    @GetMapping("/report/list")
    public ResponseEntity<UserAdminResponseDto.ViewAllReportedPost> loadReportedPost(@Valid @RequestHeader String Authorization) {

        UserAdminResponseDto.ViewAllReportedPost result = new UserAdminResponseDto.ViewAllReportedPost();

        //토큰 검증
        jwtTokenValidator.validateAccessToken(Authorization);

        //토큰으로 유저 권한 확인 -> ADMIN 이 아니면 에러
        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN"))
            throw new AccountException(ErrorType.USER_RESTRICTED);

        result.setEvaluatePostReports(userAdminService.getReportedEvaluateList());
        result.setExamPostReports(userAdminService.getReportedExamList());

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    
    // 강의평가에 관련된 신고 게시글 자세히 보기
    @GetMapping("/report/evaluate/")
    public ResponseEntity<EvaluatePostReport> loadDetailReportedEvaluatePost(
            @Valid @RequestHeader String Authorization, @Valid @RequestParam Long target) {

        // 토큰 검증
        jwtTokenValidator.validateAccessToken(Authorization);

        //토큰으로 유저 권한 확인 -> ADMIN 이 아니면 에러
        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN"))
            throw new AccountException(ErrorType.USER_RESTRICTED);

        if (evaluateReportRepository.findById(target).isEmpty()) {
            throw new AccountException(ErrorType.SERVER_ERROR);
        }

        EvaluatePostReport evaluatePostReport = evaluateReportRepository.findById(target).get();

        return ResponseEntity.status(HttpStatus.OK).body(evaluatePostReport);
    }

    // 시험정보에 관련된 신고 게시글 자세히 보기
    @GetMapping("/report/exam/")
    public ResponseEntity<ExamPostReport> loadDetailReportedExamPost(
            @Valid @RequestHeader String Authorization, @Valid @RequestParam Long target) {

        // 토큰 검증
        jwtTokenValidator.validateAccessToken(Authorization);

        //토큰으로 유저 권한 확인 -> ADMIN 이 아니면 에러
        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN"))
            throw new AccountException(ErrorType.USER_RESTRICTED);

        if (evaluateReportRepository.findById(target).isEmpty()) {
            throw new AccountException(ErrorType.SERVER_ERROR);
        }

        ExamPostReport examPostReport = examReportRepository.findById(target).get();

        return ResponseEntity.status(HttpStatus.OK).body(examPostReport);
    }
}
