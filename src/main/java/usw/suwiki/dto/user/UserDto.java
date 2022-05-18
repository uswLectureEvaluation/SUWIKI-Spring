package usw.suwiki.dto.user;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class UserDto {
    @Data
    public static class JoinForm {
        private String loginId;

        private String password;

        private String email;
    }

    @Data
    public static class LoginForm {
        @NotEmpty
        private String loginId;

        @NotEmpty
        private String password;
    }

    @Data
    public static class FindIdForm {
        @NotEmpty
        private String email;
    }

    @Data
    public static class FindPasswordForm {
        @NotEmpty
        private String loginId;
        private String email;
    }

    @Data
    public static class EditMyPasswordForm {
        @NotEmpty
        private String prePassword;
        private String newPassword;
    }

    @Data
    public static class CheckIdForm {
        private String loginId;
    }

    @Data
    public static class CheckEmailForm {
        private String email;
    }

    @Data
    public static class UserQuitForm {
        private String loginId;
        private String password;
    }

    @Data
    public static class EvaluateReportForm {
        private Long evaluateIdx;
        private String content;
    }

    @Data
    public static class ExamReportForm {
        private Long examIdx;
        private String content;
    }
}
