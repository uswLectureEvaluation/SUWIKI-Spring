package usw.suwiki.domain.user.user.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public class UserRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinForm {

        private String loginId;
        private String password;
        private String email;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginForm {

        @NotEmpty
        private String loginId;
        @NotEmpty
        private String password;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FindIdForm {

        @NotEmpty
        private String email;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FindPasswordForm {

        @NotBlank
        private String loginId;
        @NotBlank
        private String email;
    }

    @Getter
    @NoArgsConstructor
    public static class EditMyPasswordForm {

        @NotBlank
        private String prePassword;
        @NotBlank
        private String newPassword;

        public EditMyPasswordForm(String prePassword, String newPassword) {
            this.prePassword = prePassword;
            this.newPassword = newPassword;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class CheckLoginIdForm {

        @NotBlank
        private String loginId;

        public CheckLoginIdForm(String loginId) {
            this.loginId = loginId;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class CheckEmailForm {
        @NotBlank
        private String email;

        public CheckEmailForm(String email) {
            this.email = email;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class UserQuitForm {

        @NotBlank
        private String loginId;
        @NotBlank
        private String password;

        public UserQuitForm(String loginId, String password) {
            this.loginId = loginId;
            this.password = password;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class EvaluateReportForm {

        @NotEmpty
        private Long evaluateIdx;
    }

    @Getter
    @NoArgsConstructor
    public static class ExamReportForm {

        @NotEmpty
        private Long examIdx;
    }
}
