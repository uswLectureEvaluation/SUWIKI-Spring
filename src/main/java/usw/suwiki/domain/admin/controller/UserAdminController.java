package usw.suwiki.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.*;
import usw.suwiki.domain.admin.dto.UserAdminResponseDto.LoadAllReportedPostForm;
import usw.suwiki.domain.admin.service.usecase.*;
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

    private final UserAdminJwtValidateUseCase userAdminJwtValidateUseCase;
    private final UserAdminLoginUseCase userAdminLoginUseCase;
    private final UserAdminRestrictPostUseCase userAdminRestrictPostUseCase;
    private final UserAdminBlackListPostUseCase userAdminBlackListPostUseCase;
    private final UserAdminNoProblemPostUseCase userAdminNoProblemPostUseCase;
    private final UserAdminLoadReportingPostUseCase userAdminLoadReportingPostUseCase;
    private final UserAdminLoadDetailReportingPostUseCase userAdminLoadDetailReportingPostUseCase;

    // 관리자 전용 로그인 API
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> administratorLogin(@Valid @RequestBody LoginForm loginForm) {
        return ResponseEntity
                .ok()
                .body(userAdminLoginUseCase.adminLogin(loginForm));
    }

    // 강의평가 게시물 정지 먹이기
    @PostMapping("/restrict/evaluate-post")
    public ResponseEntity<Map<String, Boolean>> restrictEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody EvaluatePostRestrictForm evaluatePostRestrictForm) {

        userAdminJwtValidateUseCase.execute(Authorization);

        return ResponseEntity
                .ok()
                .body(userAdminRestrictPostUseCase.restrictEvaluatePost(evaluatePostRestrictForm));
    }

    // 시험정보 게시물 정지 먹이기
    @PostMapping("/restrict/exam-post")
    public ResponseEntity<Map<String, Boolean>> restrictExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody ExamPostRestrictForm examPostRestrictForm) {

        userAdminJwtValidateUseCase.execute(Authorization);

        return ResponseEntity
                .ok()
                .body(userAdminRestrictPostUseCase.restrictExamPost(examPostRestrictForm));
    }


    // 강의평가 게시물 블랙리스트 먹이기
    @PostMapping("/blacklist/evaluate-post")
    public ResponseEntity<Map<String, Boolean>> banEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody EvaluatePostBlacklistForm evaluatePostBlacklistForm) {

        userAdminJwtValidateUseCase.execute(Authorization);

        return ResponseEntity
                .ok()
                .body(userAdminBlackListPostUseCase.executeEvaluatePost(evaluatePostBlacklistForm));
    }

    // 시험정보 게시물 블랙리스트 먹이기
    @PostMapping("/blacklist/exam-post")
    public ResponseEntity<Map<String, Boolean>> banExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody ExamPostBlacklistForm examPostBlacklistForm) {

        userAdminJwtValidateUseCase.execute(Authorization);

        return ResponseEntity
                .ok()
                .body(userAdminBlackListPostUseCase.executeExamPost(examPostBlacklistForm));
    }

    // 이상 없는 신고 강의평가 게시글이면 지워주기
    @PostMapping("/no-problem/evaluate-post")
    public ResponseEntity<Map<String, Boolean>> noProblemEv(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody EvaluatePostNoProblemForm evaluatePostNoProblemForm) {

        userAdminJwtValidateUseCase.execute(Authorization);

        return ResponseEntity
                .ok()
                .body(userAdminNoProblemPostUseCase.executeEvaluatePost(evaluatePostNoProblemForm));
    }

    // 이상 없는 신고 시험정보 게시글이면 지워주기
    @PostMapping("/no-problem/exam-post")
    public ResponseEntity<Map<String, Boolean>> noProblemEx(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody ExamPostNoProblemForm examPostNoProblemForm) {

        userAdminJwtValidateUseCase.execute(Authorization);

        return ResponseEntity
                .ok()
                .body(userAdminNoProblemPostUseCase.executeExamPost(examPostNoProblemForm));
    }

    // 신고받은 게시글 리스트 불러오기
    @GetMapping("/report/list")
    public ResponseEntity<LoadAllReportedPostForm> loadReportedPost(
            @Valid @RequestHeader String Authorization) {

        userAdminJwtValidateUseCase.execute(Authorization);

        return ResponseEntity
                .ok()
                .body(userAdminLoadReportingPostUseCase.execute());
    }


    // 강의평가에 관련된 신고 게시글 자세히 보기
    @GetMapping("/report/evaluate/")
    public ResponseEntity<EvaluatePostReport> loadDetailReportedEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestParam Long target) {

        userAdminJwtValidateUseCase.execute(Authorization);
        return ResponseEntity
                .ok()
                .body(userAdminLoadDetailReportingPostUseCase.executeEvaluatePost(target));
    }

    // 시험정보에 관련된 신고 게시글 자세히 보기
    @GetMapping("/report/exam/")
    public ResponseEntity<ExamPostReport> loadDetailReportedExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestParam Long target) {

        userAdminJwtValidateUseCase.execute(Authorization);
        return ResponseEntity
                .ok()
                .body(userAdminLoadDetailReportingPostUseCase.executeExamPost(target));
    }
}
