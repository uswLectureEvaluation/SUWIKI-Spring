package usw.suwiki.api.report;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.domain.user.service.UserBusinessService;
import usw.suwiki.statistics.annotation.ApiLogger;

import javax.validation.Valid;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.domain.user.dto.UserRequestDto.EvaluateReportForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.ExamReportForm;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/v2/report")
@RequiredArgsConstructor
public class PostReportControllerV2 {
    private final UserBusinessService userBusinessService;

    @ApiLogger(option = "user")
    @PostMapping("/evaluate-posts")
    @ResponseStatus(OK)
    public Map<String, Boolean> reportEvaluate(
        @Valid @RequestBody EvaluateReportForm evaluateReportForm,
        @Valid @RequestHeader String Authorization
    ) {
        return userBusinessService.executeReportEvaluatePost(evaluateReportForm, Authorization);
    }

    @ApiLogger(option = "user")
    @PostMapping("/exam-posts")
    @ResponseStatus(OK)
    public Map<String, Boolean> reportExam(
        @Valid @RequestBody ExamReportForm examReportForm,
        @Valid @RequestHeader String Authorization
    ) {
        return userBusinessService.executeReportExamPost(examReportForm, Authorization);
    }
}
