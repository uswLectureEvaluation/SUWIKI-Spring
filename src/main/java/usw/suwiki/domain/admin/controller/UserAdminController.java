package usw.suwiki.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.*;
import usw.suwiki.domain.admin.dto.UserAdminResponseDto.LoadAllReportedPostForm;
import usw.suwiki.domain.admin.service.*;
import usw.suwiki.domain.postreport.entity.EvaluatePostReport;
import usw.suwiki.domain.postreport.entity.ExamPostReport;
import usw.suwiki.domain.user.dto.UserRequestDto.LoginForm;

import javax.validation.Valid;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@CrossOrigin(origins = "https://suwikiman.netlify.app/", allowedHeaders = "*")
public class UserAdminController {

    private final UserAdminJwtValidateService userAdminJwtValidateService;
    private final UserAdminLoginService userAdminLoginService;
    private final UserAdminRestrictPostService userAdminRestrictPostService;
    private final UserAdminBlackListPostService userAdminBlackListPostService;
    private final UserAdminNoProblemPostService userAdminNoProblemPostService;
    private final UserAdminLoadReportingPostService userAdminLoadReportingPostService;
    private final UserAdminLoadDetailReportingPostService userAdminLoadDetailReportingPostService;

    // 관리자 전용 로그인 API
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> administratorLogin(@Valid @RequestBody LoginForm loginForm) {
        return ResponseEntity
                .ok()
                .body(userAdminLoginService.adminLogin(loginForm));
    }

    // 강의평가 게시물 정지 먹이기
    @PostMapping("/restrict/evaluate-post")
    public ResponseEntity<Map<String, Boolean>> restrictEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody EvaluatePostRestrictForm evaluatePostRestrictForm) {

        userAdminJwtValidateService.execute(Authorization);

        return ResponseEntity
                .ok()
                .body(userAdminRestrictPostService.restrictEvaluatePost(evaluatePostRestrictForm));
    }

    // 시험정보 게시물 정지 먹이기
    @PostMapping("/restrict/exam-post")
    public ResponseEntity<Map<String, Boolean>> restrictExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody ExamPostRestrictForm examPostRestrictForm) {

        userAdminJwtValidateService.execute(Authorization);

        return ResponseEntity
                .ok()
                .body(userAdminRestrictPostService.restrictExamPost(examPostRestrictForm));
    }


    // 강의평가 게시물 블랙리스트 먹이기
    @PostMapping("/blacklist/evaluate-post")
    public ResponseEntity<Map<String, Boolean>> banEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody EvaluatePostBlacklistForm evaluatePostBlacklistForm) {

        userAdminJwtValidateService.execute(Authorization);

        return ResponseEntity
                .ok()
                .body(userAdminBlackListPostService.executeEvaluatePost(evaluatePostBlacklistForm));
    }

    // 시험정보 게시물 블랙리스트 먹이기
    @PostMapping("/blacklist/exam-post")
    public ResponseEntity<Map<String, Boolean>> banExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody ExamPostBlacklistForm examPostBlacklistForm) {

        userAdminJwtValidateService.execute(Authorization);

        return ResponseEntity
                .ok()
                .body(userAdminBlackListPostService.executeExamPost(examPostBlacklistForm));
    }

    // 이상 없는 신고 강의평가 게시글이면 지워주기
    @PostMapping("/no-problem/evaluate-post")
    public ResponseEntity<Map<String, Boolean>> noProblemEv(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody EvaluatePostNoProblemForm evaluatePostNoProblemForm) {

        userAdminJwtValidateService.execute(Authorization);

        return ResponseEntity
                .ok()
                .body(userAdminNoProblemPostService.executeEvaluatePost(evaluatePostNoProblemForm));
    }

    // 이상 없는 신고 시험정보 게시글이면 지워주기
    @PostMapping("/no-problem/exam-post")
    public ResponseEntity<Map<String, Boolean>> noProblemEx(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody ExamPostNoProblemForm examPostNoProblemForm) {

        userAdminJwtValidateService.execute(Authorization);

        return ResponseEntity
                .ok()
                .body(userAdminNoProblemPostService.executeExamPost(examPostNoProblemForm));
    }

    // 신고받은 게시글 리스트 불러오기
    @GetMapping("/report/list")
    public ResponseEntity<LoadAllReportedPostForm> loadReportedPost(
            @Valid @RequestHeader String Authorization) {

        userAdminJwtValidateService.execute(Authorization);

        return ResponseEntity
                .ok()
                .body(userAdminLoadReportingPostService.execute());
    }


    // 강의평가에 관련된 신고 게시글 자세히 보기
    @GetMapping("/report/evaluate/")
    public ResponseEntity<EvaluatePostReport> loadDetailReportedEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestParam Long target) {

        userAdminJwtValidateService.execute(Authorization);
        return ResponseEntity
                .ok()
                .body(userAdminLoadDetailReportingPostService.executeEvaluatePost(target));
    }

    // 시험정보에 관련된 신고 게시글 자세히 보기
    @GetMapping("/report/exam/")
    public ResponseEntity<ExamPostReport> loadDetailReportedExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestParam Long target) {

        userAdminJwtValidateService.execute(Authorization);
        return ResponseEntity
                .ok()
                .body(userAdminLoadDetailReportingPostService.executeExamPost(target));
    }
}
