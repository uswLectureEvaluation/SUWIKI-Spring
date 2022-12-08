package usw.suwiki.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.postreport.entity.EvaluatePostReport;
import usw.suwiki.domain.postreport.entity.ExamPostReport;

import java.util.List;
import java.util.Map;

public class UserAdminResponseDto {

    public static class SuccessFlagForm {
        private Map<String, Boolean> flag;
    }


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
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