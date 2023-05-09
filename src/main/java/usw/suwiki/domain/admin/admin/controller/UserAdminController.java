package usw.suwiki.domain.admin.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.admin.admin.dto.UserAdminRequestDto.*;
import usw.suwiki.domain.admin.admin.dto.UserAdminResponseDto.LoadAllReportedPostForm;
import usw.suwiki.domain.admin.admin.service.UserAdminBusinessService;
import usw.suwiki.domain.postreport.EvaluatePostReport;
import usw.suwiki.domain.postreport.ExamPostReport;
import usw.suwiki.domain.user.user.controller.dto.UserRequestDto.LoginForm;
import usw.suwiki.global.annotation.ApiLogger;

import javax.validation.Valid;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserAdminController {

    private final UserAdminBusinessService userAdminBusinessService;

    // 관리자 전용 로그인 API
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/login")
    public Map<String, String> administratorLogin(
            @Valid @RequestBody LoginForm loginForm
    ) {
        return userAdminBusinessService.executeAdminLogin(loginForm);
    }

    // 강의평가 게시물 정지 먹이기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/restrict/evaluate-post")
    public Map<String, Boolean> restrictEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody EvaluatePostRestrictForm evaluatePostRestrictForm
    ) {
        return userAdminBusinessService.executeRestrictEvaluatePost(Authorization, evaluatePostRestrictForm);
    }

    // 시험정보 게시물 정지 먹이기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/restrict/exam-post")
    public Map<String, Boolean> restrictExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody ExamPostRestrictForm examPostRestrictForm
    ) {
        return userAdminBusinessService.executeRestrictExamPost(Authorization, examPostRestrictForm);
    }


    // 강의평가 게시물 블랙리스트 먹이기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/blacklist/evaluate-post")
    public Map<String, Boolean> banEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody EvaluatePostBlacklistForm evaluatePostBlacklistForm
    ) {
        return userAdminBusinessService.executeBlackListEvaluatePost(Authorization, evaluatePostBlacklistForm);
    }

    // 시험정보 게시물 블랙리스트 먹이기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/blacklist/exam-post")
    public Map<String, Boolean> banExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody ExamPostBlacklistForm examPostBlacklistForm
    ) {
        return userAdminBusinessService.executeBlackListExamPost(Authorization, examPostBlacklistForm);
    }

    // 이상 없는 신고 강의평가 게시글이면 지워주기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/no-problem/evaluate-post")
    public Map<String, Boolean> noProblemEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody EvaluatePostNoProblemForm evaluatePostNoProblemForm
    ) {
        return userAdminBusinessService.executeNoProblemEvaluatePost(Authorization, evaluatePostNoProblemForm);
    }

    // 이상 없는 신고 시험정보 게시글이면 지워주기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/no-problem/exam-post")
    public Map<String, Boolean> noProblemExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody ExamPostNoProblemForm examPostNoProblemForm
    ) {
        return userAdminBusinessService.executeNoProblemExamPost(Authorization, examPostNoProblemForm);
    }

    // 신고받은 게시글 리스트 불러오기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @GetMapping("/report/list")
    public LoadAllReportedPostForm loadReportedPost(
            @Valid @RequestHeader String Authorization
    ) {
        return userAdminBusinessService.executeLoadAllReportedPosts(Authorization);
    }


    // 강의평가에 관련된 신고 게시글 자세히 보기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @GetMapping("/report/evaluate/")
    public EvaluatePostReport loadDetailReportedEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestParam Long target
    ) {
        return userAdminBusinessService.executeLoadDetailReportedEvaluatePost(Authorization, target);
    }

    // 시험정보에 관련된 신고 게시글 자세히 보기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @GetMapping("/report/exam/")
    public ExamPostReport loadDetailReportedExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestParam Long target
    ) {
        return userAdminBusinessService.executeLoadDetailReportedExamPost(Authorization, target);
    }
}
