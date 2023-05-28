package usw.suwiki.domain.admin.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public class UserAdminRequestDto {

    public record EvaluatePostNoProblemForm(
            @NotEmpty Long evaluateIdx
    ) {
    }

    public record ExamPostNoProblemForm(
            @NotEmpty Long examIdx
    ) {
    }

    public record EvaluatePostRestrictForm(
            @NotEmpty Long evaluateIdx,
            @NotEmpty Long restrictingDate,
            @NotBlank String restrictingReason,
            @NotBlank String judgement
    ) {
    }

    public record ExamPostRestrictForm(
            @NotEmpty Long examIdx,
            @NotEmpty Long restrictingDate,
            @NotBlank String restrictingReason,
            @NotBlank String judgement
    ) {
    }

    public record EvaluatePostBlacklistForm(
            @NotEmpty Long evaluateIdx,
            @NotBlank String bannedReason,
            @NotBlank String judgement
    ) {
    }

    public record ExamPostBlacklistForm(
            @NotEmpty Long examIdx,
            @NotBlank String bannedReason,
            @NotBlank String judgement
    ) {
    }
}
