package usw.suwiki.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

public class UserRequestDto {

    @Getter
    @NoArgsConstructor
    public static class JoinForm {
        private String loginId;

        private String password;

        private String email;
    }

    @Getter
    @NoArgsConstructor
    public static class LoginForm {
        @NotEmpty
        private String loginId;

        @NotEmpty
        private String password;
    }

    @Getter
    @NoArgsConstructor
    public static class FindIdForm {
        @NotEmpty
        private String email;
    }

    @Getter
    @NoArgsConstructor
    public static class FindPasswordForm {
        @NotEmpty
        private String loginId;
        private String email;
    }

    @Getter
    @NoArgsConstructor
    public static class EditMyPasswordForm {
        @NotEmpty
        private String prePassword;
        private String newPassword;
    }

    @Getter
    @NoArgsConstructor
    public static class CheckLoginIdForm {
        private String loginId;
    }

    @Getter
    @NoArgsConstructor
    public static class CheckEmailForm {
        private String email;
    }

    @Getter
    @NoArgsConstructor
    public static class UserQuitForm {
        private String loginId;
        private String password;
    }

    @Getter
    @NoArgsConstructor
    public static class EvaluateReportForm {
        private Long evaluateIdx;
    }

    @Getter
    @NoArgsConstructor
    public static class ExamReportForm {
        private Long examIdx;
    }
}
