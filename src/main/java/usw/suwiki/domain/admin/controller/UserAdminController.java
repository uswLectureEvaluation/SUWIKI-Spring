package usw.suwiki.domain.admin.controller;

import static org.springframework.http.HttpStatus.OK;

import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.EvaluatePostBlacklistForm;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.EvaluatePostNoProblemForm;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.EvaluatePostRestrictForm;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.ExamPostBlacklistForm;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.ExamPostNoProblemForm;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.ExamPostRestrictForm;
import usw.suwiki.domain.admin.dto.UserAdminResponseDto.LoadAllReportedPostForm;
import usw.suwiki.domain.admin.service.UserAdminBlackListPostService;
import usw.suwiki.domain.admin.service.UserAdminLoadDetailReportingPostService;
import usw.suwiki.domain.admin.service.UserAdminLoadReportingPostService;
import usw.suwiki.domain.admin.service.UserAdminNoProblemPostService;
import usw.suwiki.domain.admin.service.UserAdminRestrictPostService;
import usw.suwiki.domain.admin.service.UserAdminService;
import usw.suwiki.domain.postreport.entity.EvaluatePostReport;
import usw.suwiki.domain.postreport.entity.ExamPostReport;
import usw.suwiki.domain.user.user.dto.UserRequestDto.LoginForm;
import usw.suwiki.global.annotation.ApiLogger;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserAdminController {

    private final UserAdminService userAdminService;
    private final UserAdminRestrictPostService userAdminRestrictPostService;
    private final UserAdminBlackListPostService userAdminBlackListPostService;
    private final UserAdminNoProblemPostService userAdminNoProblemPostService;
    private final UserAdminLoadReportingPostService userAdminLoadReportingPostService;
    private final UserAdminLoadDetailReportingPostService userAdminLoadDetailReportingPostService;

    // 관리자 전용 로그인 API
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/login")
    public Map<String, String> administratorLogin(
        @Valid @RequestBody LoginForm loginForm
    ) {
        return userAdminService.adminLogin(loginForm);
    }

    // 강의평가 게시물 정지 먹이기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/restrict/evaluate-post")
    public Map<String, Boolean> restrictEvaluatePost(
        @Valid @RequestHeader String Authorization,
        @Valid @RequestBody EvaluatePostRestrictForm evaluatePostRestrictForm
    ) {
        userAdminService.executeValidateAdmin(Authorization);
        return userAdminRestrictPostService.restrictEvaluatePost(evaluatePostRestrictForm);
    }

    // 시험정보 게시물 정지 먹이기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/restrict/exam-post")
    public Map<String, Boolean> restrictExamPost(
        @Valid @RequestHeader String Authorization,
        @Valid @RequestBody ExamPostRestrictForm examPostRestrictForm
    ) {
        userAdminService.executeValidateAdmin(Authorization);
        return userAdminRestrictPostService.restrictExamPost(examPostRestrictForm);
    }


    // 강의평가 게시물 블랙리스트 먹이기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/blacklist/evaluate-post")
    public Map<String, Boolean> banEvaluatePost(
        @Valid @RequestHeader String Authorization,
        @Valid @RequestBody EvaluatePostBlacklistForm evaluatePostBlacklistForm
    ) {
        userAdminService.executeValidateAdmin(Authorization);
        return userAdminBlackListPostService.executeEvaluatePost(evaluatePostBlacklistForm);
    }

    // 시험정보 게시물 블랙리스트 먹이기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/blacklist/exam-post")
    public Map<String, Boolean> banExamPost(
        @Valid @RequestHeader String Authorization,
        @Valid @RequestBody ExamPostBlacklistForm examPostBlacklistForm
    ) {
        userAdminService.executeValidateAdmin(Authorization);
        return userAdminBlackListPostService.executeExamPost(examPostBlacklistForm);
    }

    // 이상 없는 신고 강의평가 게시글이면 지워주기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/no-problem/evaluate-post")
    public Map<String, Boolean> noProblemEvaluatePost(
        @Valid @RequestHeader String Authorization,
        @Valid @RequestBody EvaluatePostNoProblemForm evaluatePostNoProblemForm
    ) {
        userAdminService.executeValidateAdmin(Authorization);
        return userAdminNoProblemPostService.executeEvaluatePost(evaluatePostNoProblemForm);
    }

    // 이상 없는 신고 시험정보 게시글이면 지워주기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/no-problem/exam-post")
    public Map<String, Boolean> noProblemExamPost(
        @Valid @RequestHeader String Authorization,
        @Valid @RequestBody ExamPostNoProblemForm examPostNoProblemForm
    ) {
        userAdminService.executeValidateAdmin(Authorization);
        return userAdminNoProblemPostService.executeExamPost(examPostNoProblemForm);
    }

    // 신고받은 게시글 리스트 불러오기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @GetMapping("/report/list")
    public LoadAllReportedPostForm loadReportedPost(
        @Valid @RequestHeader String Authorization
    ) {
        userAdminService.executeValidateAdmin(Authorization);
        return userAdminLoadReportingPostService.execute();
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
        return userAdminLoadDetailReportingPostService.executeEvaluatePost(target);
    }

    // 시험정보에 관련된 신고 게시글 자세히 보기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @GetMapping("/report/exam/")
    public ResponseEntity<ExamPostReport> loadDetailReportedExamPost(
        @Valid @RequestHeader String Authorization,
        @Valid @RequestParam Long target
    ) {

        userAdminService.executeValidateAdmin(Authorization);
        return ResponseEntity
            .ok()
            .body(userAdminLoadDetailReportingPostService.executeExamPost(target));
    }
}
