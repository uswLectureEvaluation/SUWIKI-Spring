package usw.suwiki.domain.admin.controller;

import static org.springframework.http.HttpStatus.OK;

import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.domain.admin.controller.dto.UserAdminRequestDto.EvaluatePostBlacklistForm;
import usw.suwiki.domain.admin.controller.dto.UserAdminRequestDto.EvaluatePostNoProblemForm;
import usw.suwiki.domain.admin.controller.dto.UserAdminRequestDto.EvaluatePostRestrictForm;
import usw.suwiki.domain.admin.controller.dto.UserAdminRequestDto.ExamPostBlacklistForm;
import usw.suwiki.domain.admin.controller.dto.UserAdminRequestDto.ExamPostNoProblemForm;
import usw.suwiki.domain.admin.controller.dto.UserAdminRequestDto.ExamPostRestrictForm;
import usw.suwiki.domain.admin.controller.dto.UserAdminResponseDto.LoadAllReportedPostForm;
import usw.suwiki.domain.admin.service.AdminBusinessService;
import usw.suwiki.domain.postreport.EvaluatePostReport;
import usw.suwiki.domain.postreport.ExamPostReport;
import usw.suwiki.domain.user.user.controller.dto.UserRequestDto.LoginForm;
import usw.suwiki.global.annotation.ApiLogger;
import usw.suwiki.global.annotation.JWTVerify;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdminController {

    private final AdminBusinessService adminBusinessService;

    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/login")
    public Map<String, String> administratorLogin(
        @Valid @RequestBody LoginForm loginForm
    ) {
        return adminBusinessService.executeAdminLogin(loginForm);
    }

    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/restrict/evaluate-posts")
    public Map<String, Boolean> restrictEvaluatePost(
        @Valid @RequestHeader String Authorization,
        @Valid @RequestBody EvaluatePostRestrictForm evaluatePostRestrictForm
    ) {
        return adminBusinessService.executeRestrictEvaluatePost(evaluatePostRestrictForm);
    }

    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/restrict/exam-post")
    public Map<String, Boolean> restrictExamPost(
        @Valid @RequestHeader String Authorization,
        @Valid @RequestBody ExamPostRestrictForm examPostRestrictForm
    ) {
        return adminBusinessService.executeRestrictExamPost(examPostRestrictForm);
    }


    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/blacklist/evaluate-post")
    public Map<String, Boolean> banEvaluatePost(
        @Valid @RequestHeader String Authorization,
        @Valid @RequestBody EvaluatePostBlacklistForm evaluatePostBlacklistForm
    ) {
        return adminBusinessService.executeBlackListEvaluatePost(evaluatePostBlacklistForm);
    }

    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/blacklist/exam-post")
    public Map<String, Boolean> banExamPost(
        @Valid @RequestHeader String Authorization,
        @Valid @RequestBody ExamPostBlacklistForm examPostBlacklistForm
    ) {
        return adminBusinessService.executeBlackListExamPost(examPostBlacklistForm);
    }

    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @DeleteMapping("/no-problem/evaluate-post")
    public Map<String, Boolean> noProblemEvaluatePost(
        @Valid @RequestHeader String Authorization,
        @Valid @RequestBody EvaluatePostNoProblemForm evaluatePostNoProblemForm
    ) {
        return adminBusinessService.executeNoProblemEvaluatePost(evaluatePostNoProblemForm);
    }

    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @DeleteMapping("/no-problem/exam-post")
    public Map<String, Boolean> noProblemExamPost(
        @Valid @RequestHeader String Authorization,
        @Valid @RequestBody ExamPostNoProblemForm examPostNoProblemForm
    ) {
        return adminBusinessService.executeNoProblemExamPost(examPostNoProblemForm);
    }

    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @GetMapping("/report/list")
    public LoadAllReportedPostForm loadReportedPost(
        @Valid @RequestHeader String Authorization
    ) {
        return adminBusinessService.executeLoadAllReportedPosts();
    }


    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @GetMapping("/report/evaluate/")
    public EvaluatePostReport loadDetailReportedEvaluatePost(
        @Valid @RequestHeader String Authorization,
        @Valid @RequestParam Long target
    ) {
        return adminBusinessService.executeLoadDetailReportedEvaluatePost(target);
    }

    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @GetMapping("/report/exam/")
    public ExamPostReport loadDetailReportedExamPost(
        @Valid @RequestHeader String Authorization,
        @Valid @RequestParam Long target
    ) {
        return adminBusinessService.executeLoadDetailReportedExamPost(target);
    }
}
