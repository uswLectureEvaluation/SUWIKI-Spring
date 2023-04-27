package usw.suwiki.domain.user.user.dto;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

        public CheckLoginIdForm(String loginId) {
            this.loginId = loginId;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class CheckEmailForm {

        private String email;

        public CheckEmailForm(String email) {
            this.email = email;
        }
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
