package usw.suwiki.api.admin;

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
import usw.suwiki.auth.core.annotation.JWTVerify;
import usw.suwiki.domain.user.dto.UserAdminResponseDto;
import usw.suwiki.domain.user.service.AdminBusinessService;
import usw.suwiki.report.EvaluatePostReport;
import usw.suwiki.report.ExamPostReport;
import usw.suwiki.statistics.annotation.ApiLogger;

import javax.validation.Valid;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.EvaluatePostBlacklistForm;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.EvaluatePostNoProblemForm;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.EvaluatePostRestrictForm;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.ExamPostBlacklistForm;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.ExamPostNoProblemForm;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.ExamPostRestrictForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.LoginForm;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/v2/admin")
@RequiredArgsConstructor
public class AdminControllerV2 {
  private final AdminBusinessService adminBusinessService;

  @ApiLogger(option = "admin")
  @PostMapping("/login")
  @ResponseStatus(OK)
  public Map<String, String> administratorLogin(@Valid @RequestBody LoginForm loginForm) {
    return adminBusinessService.executeAdminLogin(loginForm);
  }

  @JWTVerify(option = "ADMIN")
  @ApiLogger(option = "admin")
  @PostMapping("/evaluate-posts/restrict")
  @ResponseStatus(OK)
  public Map<String, Boolean> restrictEvaluatePost(
    @Valid @RequestHeader String Authorization,
    @Valid @RequestBody EvaluatePostRestrictForm evaluatePostRestrictForm
  ) {
    return adminBusinessService.executeRestrictEvaluatePost(evaluatePostRestrictForm);
  }

  @JWTVerify(option = "ADMIN")
  @ResponseStatus(OK)
  @ApiLogger(option = "admin")
  @PostMapping("/exam-post/restrict")
  public Map<String, Boolean> restrictExamPost(
    @Valid @RequestHeader String Authorization,
    @Valid @RequestBody ExamPostRestrictForm examPostRestrictForm
  ) {
    return adminBusinessService.executeRestrictExamPost(examPostRestrictForm);
  }


  @JWTVerify(option = "ADMIN")
  @ResponseStatus(OK)
  @ApiLogger(option = "admin")
  @PostMapping("/evaluate-post/blacklist")
  public Map<String, Boolean> banEvaluatePost(
    @Valid @RequestHeader String Authorization,
    @Valid @RequestBody EvaluatePostBlacklistForm evaluatePostBlacklistForm
  ) {
    return adminBusinessService.executeBlackListEvaluatePost(evaluatePostBlacklistForm);
  }

  @JWTVerify(option = "ADMIN")
  @ResponseStatus(OK)
  @ApiLogger(option = "admin")
  @PostMapping("/exam-post/blacklist")
  public Map<String, Boolean> banExamPost(
    @Valid @RequestHeader String Authorization,
    @Valid @RequestBody ExamPostBlacklistForm examPostBlacklistForm
  ) {
    return adminBusinessService.executeBlackListExamPost(examPostBlacklistForm);
  }

  @JWTVerify(option = "ADMIN")
  @ResponseStatus(OK)
  @ApiLogger(option = "admin")
  @DeleteMapping("/evaluate-post")
  public Map<String, Boolean> noProblemEvaluatePost(
    @Valid @RequestHeader String Authorization,
    @Valid @RequestBody EvaluatePostNoProblemForm evaluatePostNoProblemForm
  ) {
    return adminBusinessService.executeNoProblemEvaluatePost(evaluatePostNoProblemForm);
  }

  @JWTVerify(option = "ADMIN")
  @ResponseStatus(OK)
  @ApiLogger(option = "admin")
  @DeleteMapping("/exam-post")
  public Map<String, Boolean> noProblemExamPost(
    @Valid @RequestHeader String Authorization,
    @Valid @RequestBody ExamPostNoProblemForm examPostNoProblemForm
  ) {
    return adminBusinessService.executeNoProblemExamPost(examPostNoProblemForm);
  }

  @JWTVerify(option = "ADMIN")
  @ApiLogger(option = "admin")
  @GetMapping("/reported-posts")
  @ResponseStatus(OK)
  public UserAdminResponseDto.LoadAllReportedPostForm loadReportedPost(@RequestHeader String Authorization) {
    return adminBusinessService.executeLoadAllReportedPosts();
  }


  @JWTVerify(option = "ADMIN")
  @ApiLogger(option = "admin")
  @GetMapping("/reported-evaluate/")
  @ResponseStatus(OK)
  public EvaluatePostReport loadDetailReportedEvaluatePost(@RequestHeader String Authorization, @RequestParam Long target) {
    return adminBusinessService.executeLoadDetailReportedEvaluatePost(target);
  }

  @ApiLogger(option = "admin")
  @GetMapping("/reported-exam/")
  @ResponseStatus(OK)
  public ExamPostReport loadDetailReportedExamPost(@RequestHeader String Authorization, @RequestParam Long target) {
    return adminBusinessService.executeLoadDetailReportedExamPost(target);
  }
}
