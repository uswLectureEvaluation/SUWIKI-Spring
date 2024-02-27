package usw.suwiki.domain.admin.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UserAdminRequestDto {

    public record EvaluatePostNoProblemForm(
        @NotNull Long evaluateIdx
    ) {

    }

    public record ExamPostNoProblemForm(
        @NotNull Long examIdx
    ) {

    }

    public record EvaluatePostRestrictForm(
        @NotNull Long evaluateIdx,
        @NotNull Long restrictingDate,
        @NotBlank String restrictingReason,
        @NotBlank String judgement
    ) {

    }

    public record ExamPostRestrictForm(
        @NotNull Long examIdx,
        @NotNull Long restrictingDate,
        @NotBlank String restrictingReason,
        @NotBlank String judgement
    ) {

    }

    public record EvaluatePostBlacklistForm(
        @NotNull Long evaluateIdx,
        @NotBlank String bannedReason,
        @NotBlank String judgement
    ) {

    }

    public record ExamPostBlacklistForm(
        @NotNull Long examIdx,
        @NotBlank String bannedReason,
        @NotBlank String judgement
    ) {

    }
}
