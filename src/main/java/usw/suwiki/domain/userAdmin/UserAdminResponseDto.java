package usw.suwiki.domain.userAdmin;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.reportTarget.EvaluatePostReport;
import usw.suwiki.domain.reportTarget.ExamPostReport;

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