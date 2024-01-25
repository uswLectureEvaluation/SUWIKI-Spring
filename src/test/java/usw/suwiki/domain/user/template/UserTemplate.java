package usw.suwiki.domain.user.template;

import java.time.LocalDateTime;
import usw.suwiki.domain.user.user.Role;
import usw.suwiki.domain.user.user.User;

public class UserTemplate {

    private static final Long ID_A = 1L;
    private static final Long ID_B = 2L;
    private static final String LOGIN_ID_A = "testId";
    private static final String LOGIN_ID_B = "testId2";

    private static final String PASSWORD = "password";
    private static final String EMAIL_A = "test@suwon.ac.kr";
    private static final String EMAIL_B = "test2@suwon.ac.kr";

    private static final Boolean RESTRICTED = Boolean.FALSE;
    private static final Integer RESTRICTED_COUNT = 0;
    private static final Role ROLE_USER = Role.USER;
    private static final Role ROLE_ADMIN = Role.ADMIN;

    private static final Integer WRITTEN_EVALUATION = 0;
    private static final Integer WRITTEN_EXAM = 0;

    private static final Integer VIEW_EXAM_COUNT = 0;
    private static final Integer POINT = 0;
    private static final LocalDateTime LAST_LOGIN = LocalDateTime.now().minusHours(1);
    private static final LocalDateTime REQUESTED_QUIT_DATE = LocalDateTime.now();

    public static User createMockUser(Long id, String loginId, String password, String email, Boolean restricted, Integer restrictedCount,
                                      Role role, Integer writtenEvaluation, Integer writtenExam, Integer viewExamCount, Integer point,
                                      LocalDateTime lastLogin, LocalDateTime requestedQuitDate) {
        return User.builder()
                .id(id)
                .loginId(loginId)
                .password(password)
                .email(email)
                .restricted(restricted)
                .restrictedCount(restrictedCount)
                .role(role)
                .writtenEvaluation(writtenEvaluation)
                .writtenExam(writtenExam)
                .viewExamCount(viewExamCount)
                .point(point)
                .lastLogin(lastLogin)
                .requestedQuitDate(requestedQuitDate).build();
    }

    public static User createDummyUser() {
        return createMockUser(ID_A, LOGIN_ID_A, PASSWORD, EMAIL_A, RESTRICTED,
                RESTRICTED_COUNT, ROLE_USER, WRITTEN_EVALUATION, WRITTEN_EXAM,
                VIEW_EXAM_COUNT, POINT, LAST_LOGIN, REQUESTED_QUIT_DATE);
    }

    public static User createSecondDummyUser() {
        return createMockUser(ID_B, LOGIN_ID_B, PASSWORD, EMAIL_B, RESTRICTED,
                RESTRICTED_COUNT, ROLE_USER, WRITTEN_EVALUATION, WRITTEN_EXAM,
                VIEW_EXAM_COUNT, POINT, LAST_LOGIN, REQUESTED_QUIT_DATE);
    }
}