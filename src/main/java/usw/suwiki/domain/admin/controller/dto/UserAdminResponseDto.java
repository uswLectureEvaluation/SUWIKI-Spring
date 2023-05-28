package usw.suwiki.domain.admin.controller.dto;

import lombok.Builder;
import usw.suwiki.domain.postreport.EvaluatePostReport;
import usw.suwiki.domain.postreport.ExamPostReport;

import java.util.List;

public class UserAdminResponseDto {

    @Builder
    public record LoadAllReportedPostForm(
            List<ExamPostReport> examPostReports,
            List<EvaluatePostReport> evaluatePostReports
    ) {
    }
}