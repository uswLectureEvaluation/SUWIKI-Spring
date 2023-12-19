package usw.suwiki.template.user;

import usw.suwiki.domain.user.user.Role;
import usw.suwiki.domain.user.user.User;

import java.time.LocalDateTime;

public class UserTemplate {

    private static final Long ID_A = 1L;
    private static final String LOGIN_ID = "testId";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "test@suwon.ac.kr";
    private static final Boolean RESTRICTED = Boolean.FALSE;
    private static final Integer RESTRICTED_COUNT = 0;
    private static final Role ROLE = Role.USER;
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
        return createMockUser(ID_A, LOGIN_ID, PASSWORD, EMAIL, RESTRICTED,
                RESTRICTED_COUNT, ROLE, WRITTEN_EVALUATION, WRITTEN_EXAM,
                VIEW_EXAM_COUNT, POINT, LAST_LOGIN, REQUESTED_QUIT_DATE);
    }
}
