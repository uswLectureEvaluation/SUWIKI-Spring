package usw.suwiki.domain.admin.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserAdminRequestDto {

    @Getter @NoArgsConstructor
    public static class EvaluatePostNoProblemForm {
        private Long evaluateIdx;
    }

    @Getter @NoArgsConstructor
    public static class ExamPostNoProblemForm {
        private Long examIdx;
    }

    @Getter @NoArgsConstructor
    public static class EvaluatePostRestrictForm {
        private Long evaluateIdx;
        private Long restrictingDate;
        private String restrictingReason;
        private String judgement;
    }

    @Getter @NoArgsConstructor
    public static class ExamPostRestrictForm {
        private Long examIdx;
        private Long restrictingDate;
        private String restrictingReason;
        private String judgement;
    }

    @Getter @NoArgsConstructor
    public static class EvaluatePostBlacklistForm {
        private Long evaluateIdx;
        private String bannedReason;
        private String judgement;
    }

    @Getter @NoArgsConstructor
    public static class ExamPostBlacklistForm {
        private Long examIdx;
        private String bannedReason;
        private String judgement;
    }
}
