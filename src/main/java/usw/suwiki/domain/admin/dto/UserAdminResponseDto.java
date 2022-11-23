package usw.suwiki.domain.admin.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.reportTarget.entity.EvaluatePostReport;
import usw.suwiki.domain.reportTarget.entity.ExamPostReport;

import java.util.List;

@NoArgsConstructor
@Getter
public class UserAdminResponseDto {

    @Data
    public static class ViewAllReportedPost {
        private List<ExamPostReport> examPostReports;
        private List<EvaluatePostReport> evaluatePostReports;
    }

    @Data
    public static class ViewDetailReportedPost {
        private EvaluatePostReport evaluatePostReport;
        private ExamPostReport examPostReport;
    }
}