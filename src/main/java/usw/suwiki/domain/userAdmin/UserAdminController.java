package usw.suwiki.domain.userAdmin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.reportTarget.EvaluatePostReport;
import usw.suwiki.domain.reportTarget.ExamPostReport;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserDto;
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
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/proxy/admin")
public class UserAdminController {

    // JWT 관련 의존성
    private final JwtTokenResolver jwtTokenResolver;
    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenProvider jwtTokenProvider;

    // 유저 관련 비즈니스 로직
    private final UserService userService;

    // 관리자 계정 관련 비즈니스 로직
    private final UserAdminService userAdminService;

    private final EvaluateReportRepository evaluateReportRepository;
    private final ExamReportRepository examReportRepository;


    // 관리자 전용 로그인 API
    @PostMapping("/login")
    public ResponseEntity<HashMap<String, String>> administratorLogin(@Valid @RequestBody UserDto.LoginForm loginForm) {

        HashMap<String, String> token = new HashMap<>();

        //아이디 비밀번호 검증
        userService.matchingLoginIdWithPassword(loginForm.getLoginId(), loginForm.getPassword());

        //유저 객체 생성
        User user = userService.loadUserFromLoginId(loginForm.getLoginId());

        //액세스 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user);
        token.put("AccessToken", accessToken);

        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    
    // 강의평가 게시물 벤 먹이기
    @PostMapping("/ban/evaluate-post")
    public HashMap<String, Boolean> banEvaluatePost(@Valid @RequestHeader String Authorization,
                                                    @Valid @RequestBody UserAdminRequestDto.EvaluatePostBanForm evaluatePostBanForm) {

        //토큰 검증
        jwtTokenValidator.validateAccessToken(Authorization);

        //토큰으로 유저 권한 확인 -> ADMIN 이 아니면 에러
        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN"))
            throw new AccountException(ErrorType.USER_RESTRICTED);

        HashMap<String, Boolean> result = new HashMap<>();

        // 게시글 삭제
        Long evaluateIdx = userAdminService.banishEvaluatePost(evaluatePostBanForm);

        // 유저 블랙리스트 테이블로
        userAdminService.banUserByEvaluate(
                evaluateIdx,
                evaluatePostBanForm.getBannedTime(),
                evaluatePostBanForm.getBannedReason(),
                evaluatePostBanForm.getJudgement());

        result.put("Success", true);
        return result;
    }

    // 시험정보 게시물 벤 먹이기
    @PostMapping("/ban/exam-post")
    public HashMap<String, Boolean> banExamPost(@Valid @RequestHeader String Authorization,
                                              @Valid @RequestBody UserAdminRequestDto.ExamPostBanForm examPostBanForm) {

        //토큰 검증
        jwtTokenValidator.validateAccessToken(Authorization);

        //토큰으로 유저 권한 확인 -> ADMIN 이 아니면 에러
        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN"))
            throw new AccountException(ErrorType.USER_RESTRICTED);

        HashMap<String, Boolean> result = new HashMap<>();

        // 게시글 삭제
        Long examIdx = userAdminService.banishExamPost(examPostBanForm);

        // 유저 블랙리스트 테이블로
        userAdminService.banUserByExam(
                        examIdx,
                        examPostBanForm.getBannedTime(),
                        examPostBanForm.getBannedReason(),
                        examPostBanForm.getJudgement());

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
