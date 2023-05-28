package usw.suwiki.domain.admin.controller.dto;

public class UserAdminRequestDto {

    public record EvaluatePostNoProblemForm(Long evaluateIdx) {
    }

    public record ExamPostNoProblemForm(Long examIdx) {
    }

    public record EvaluatePostRestrictForm(
            Long evaluateIdx,
            Long restrictingDate,
            String restrictingReason,
            String judgement
    ) {
    }

    public record ExamPostRestrictForm(
            Long examIdx,
            Long restrictingDate,
            String restrictingReason,
            String judgement
    ) {
    }

    public record EvaluatePostBlacklistForm(
            Long evaluateIdx,
            String bannedReason,
            String judgement
    ) {
    }

    public record ExamPostBlacklistForm(
            Long examIdx,
            String bannedReason,
            String judgement) {
    }
}
