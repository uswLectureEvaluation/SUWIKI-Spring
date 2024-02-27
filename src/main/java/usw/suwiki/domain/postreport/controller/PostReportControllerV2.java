package usw.suwiki.domain.postreport.controller;

import static org.springframework.http.HttpStatus.OK;

import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.domain.user.user.controller.dto.UserRequestDto;
import usw.suwiki.domain.user.user.service.UserBusinessService;
import usw.suwiki.global.annotation.ApiLogger;

@RestController
@RequestMapping("/v2/report")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostReportControllerV2 {

    private final UserBusinessService userBusinessService;

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/evaluate-posts")
    public Map<String, Boolean> reportEvaluate(
        @Valid @RequestBody UserRequestDto.EvaluateReportForm evaluateReportForm,
        @Valid @RequestHeader String Authorization
    ) {
        return userBusinessService.executeReportEvaluatePost(evaluateReportForm, Authorization);
    }

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/exam-posts")
    public Map<String, Boolean> reportExam(
        @Valid @RequestBody UserRequestDto.ExamReportForm examReportForm,
        @Valid @RequestHeader String Authorization
    ) {
        return userBusinessService.executeReportExamPost(examReportForm, Authorization);
    }
}
