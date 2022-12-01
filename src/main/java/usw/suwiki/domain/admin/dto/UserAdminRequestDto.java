package usw.suwiki.domain.admin.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserAdminRequestDto {

    @Data
    public static class EvaluatePostNoProblemForm {
        private Long evaluateIdx;
    }

    @Data
    public static class ExamPostNoProblemForm {
        private Long examIdx;
    }

    @Data
    public static class EvaluatePostRestrictForm {
        private Long evaluateIdx;
        private Long restrictingDate;
        private String restrictingReason;
        private String judgement;
    }

    @Data
    public static class ExamPostRestrictForm {
        private Long examIdx;
        private Long restrictingDate;
        private String restrictingReason;
        private String judgement;
    }

    @Data
    public static class EvaluatePostBlacklistForm {
        private Long evaluateIdx;
        private String bannedReason;
        private String judgement;
    }

    @Data
    public static class ExamPostBlacklistForm {
        private Long examIdx;
        private String bannedReason;
        private String judgement;
    }
}
