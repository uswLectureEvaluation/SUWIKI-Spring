package usw.suwiki.domain.admin.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.admin.admin.dto.UserAdminRequestDto;
import usw.suwiki.domain.admin.admin.dto.UserAdminResponseDto;
import usw.suwiki.domain.admin.admin.service.UserAdminService;
import usw.suwiki.domain.postreport.entity.EvaluatePostReport;
import usw.suwiki.domain.postreport.entity.ExamPostReport;
import usw.suwiki.domain.user.user.dto.UserRequestDto.LoginForm;
import usw.suwiki.global.annotation.ApiLogger;

import javax.validation.Valid;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserAdminController {

    private final UserAdminService userAdminService;

    // 관리자 전용 로그인 API
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/login")
    public Map<String, String> administratorLogin(
            @Valid @RequestBody LoginForm loginForm
    ) {
        return userAdminService.executeAdminLogin(loginForm);
    }

    // 강의평가 게시물 정지 먹이기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/restrict/evaluate-post")
    public Map<String, Boolean> restrictEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody UserAdminRequestDto.EvaluatePostRestrictForm evaluatePostRestrictForm
    ) {
        userAdminService.executeValidateAdmin(Authorization);
        return userAdminService.executeRestrictEvaluatePost(evaluatePostRestrictForm);
    }

    // 시험정보 게시물 정지 먹이기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/restrict/exam-post")
    public Map<String, Boolean> restrictExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody UserAdminRequestDto.ExamPostRestrictForm examPostRestrictForm
    ) {
        userAdminService.executeValidateAdmin(Authorization);
        return userAdminService.executeRestrictExamPost(examPostRestrictForm);
    }


    // 강의평가 게시물 블랙리스트 먹이기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/blacklist/evaluate-post")
    public Map<String, Boolean> banEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody UserAdminRequestDto.EvaluatePostBlacklistForm evaluatePostBlacklistForm
    ) {
        userAdminService.executeValidateAdmin(Authorization);
        return userAdminService.executeBlackListEvaluatePost(evaluatePostBlacklistForm);
    }

    // 시험정보 게시물 블랙리스트 먹이기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/blacklist/exam-post")
    public Map<String, Boolean> banExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody UserAdminRequestDto.ExamPostBlacklistForm examPostBlacklistForm
    ) {
        userAdminService.executeValidateAdmin(Authorization);
        return userAdminService.executeBlackListExamPost(examPostBlacklistForm);
    }

    // 이상 없는 신고 강의평가 게시글이면 지워주기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/no-problem/evaluate-post")
    public Map<String, Boolean> noProblemEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody UserAdminRequestDto.EvaluatePostNoProblemForm evaluatePostNoProblemForm
    ) {
        userAdminService.executeValidateAdmin(Authorization);
        return userAdminService.executeEvaluatePost(evaluatePostNoProblemForm);
    }

    // 이상 없는 신고 시험정보 게시글이면 지워주기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/no-problem/exam-post")
    public Map<String, Boolean> noProblemExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody UserAdminRequestDto.ExamPostNoProblemForm examPostNoProblemForm
    ) {
        userAdminService.executeValidateAdmin(Authorization);
        return userAdminService.executeExamPost(examPostNoProblemForm);
    }

    // 신고받은 게시글 리스트 불러오기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @GetMapping("/report/list")
    public UserAdminResponseDto.LoadAllReportedPostForm loadReportedPost(
            @Valid @RequestHeader String Authorization
    ) {
        userAdminService.executeValidateAdmin(Authorization);
        return userAdminService.executeLoadAllReportedPosts();
    }


    // 강의평가에 관련된 신고 게시글 자세히 보기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @GetMapping("/report/evaluate/")
    public EvaluatePostReport loadDetailReportedEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestParam Long target
    ) {
        userAdminService.executeValidateAdmin(Authorization);
        return userAdminService.executeLoadDetailReportedEvaluatePost(target);
    }

    // 시험정보에 관련된 신고 게시글 자세히 보기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @GetMapping("/report/exam/")
    public ExamPostReport loadDetailReportedExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestParam Long target
    ) {

        userAdminService.executeValidateAdmin(Authorization);
        return userAdminService.executeLoadDetailReportedExamPost(target);
    }
}
