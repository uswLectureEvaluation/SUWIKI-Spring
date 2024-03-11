package usw.suwiki.domain.user.dto;

import lombok.Builder;
import usw.suwiki.report.evaluatepost.EvaluatePostReport;
import usw.suwiki.report.exampost.ExamPostReport;

import java.util.List;

public class UserAdminResponseDto {

    @Builder
    public record LoadAllReportedPostForm(
        List<ExamPostReport> examPostReports,
        List<EvaluatePostReport> evaluatePostReports
    ) {
    }
}
