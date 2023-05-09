package usw.suwiki.domain.admin.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.postreport.EvaluatePostReport;
import usw.suwiki.domain.postreport.ExamPostReport;

import java.util.List;

public class UserAdminResponseDto {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class LoadAllReportedPostForm {
        private List<ExamPostReport> examPostReports;
        private List<EvaluatePostReport> evaluatePostReports;
    }

    @Getter
    @NoArgsConstructor
    public static class LoadDetailReportedPostForm {
        private EvaluatePostReport evaluatePostReport;
        private ExamPostReport examPostReport;
    }
}