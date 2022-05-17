package usw.suwiki.dto.userAdmin;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.reportTarget.EvaluatePostReport;
import usw.suwiki.domain.reportTarget.ExamPostReport;

import java.util.List;

@NoArgsConstructor
@Getter
public class UserAdminDto {
    @Data
    public static class BannedTargetForm {
        private Long evaluateIdx; //강의평가 인덱스
        private Long examIdx; //시험 인덱스
        private Boolean postType; //게시글 타입
        private Long bannedTime; //정지 기간
    }

    @Data
    public static class ViewAllBannedPost {

        private List<ExamPostReport> examPostReports;
        private List<EvaluatePostReport> evaluatePostReports;
    }


}
