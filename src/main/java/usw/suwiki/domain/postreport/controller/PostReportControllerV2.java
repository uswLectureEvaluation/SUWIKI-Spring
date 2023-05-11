package usw.suwiki.domain.postreport.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.user.user.controller.dto.UserRequestDto;
import usw.suwiki.domain.user.user.service.UserBusinessService;
import usw.suwiki.global.annotation.ApiLogger;

import javax.validation.Valid;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/v2/report")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostReportControllerV2 {

    private final UserBusinessService userBusinessService;

    // 강의평가 신고 생성
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/evaluate-posts")
    public Map<String, Boolean> reportEvaluate(
            @Valid @RequestBody UserRequestDto.EvaluateReportForm evaluateReportForm,
            @Valid @RequestHeader String Authorization
    ) {
        return userBusinessService.executeReportEvaluatePost(evaluateReportForm, Authorization);
    }

    // 시험정보 신고 생성
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/exam-posts")
    public Map<String, Boolean> reportExam(
            @Valid @RequestBody UserRequestDto.ExamReportForm examReportForm,
            @Valid @RequestHeader String Authorization) {
        return userBusinessService.executeReportExamPost(examReportForm, Authorization);
    }
}
