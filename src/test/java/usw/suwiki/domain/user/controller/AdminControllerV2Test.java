package usw.suwiki.domain.user.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Connection;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import usw.suwiki.domain.admin.controller.dto.UserAdminRequestDto.EvaluatePostRestrictForm;
import usw.suwiki.domain.user.user.controller.dto.UserRequestDto.LoginForm;
import usw.suwiki.global.IntegrationTestBase;

class AdminControllerV2Test extends IntegrationTestBase {

    @BeforeAll
    public void init() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-user.sql"));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-lecture.sql"));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-exampost.sql"));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-evaluatepost.sql"));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-viewexam.sql"));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-evaluatepostreport.sql"));
        }
    }

    @SneakyThrows(Exception.class)
    @Test
    @DisplayName("관리자 로그인")
    void 관리자_로그인() {
        LoginForm loginForm = new LoginForm(
                "adminUser",
                "qwer1234!"
        );
        executePostRequestResultActions(
                "/v2/admin/login",
                loginForm
        ).andExpect(status().isOk());
    }

    @SneakyThrows(Exception.class)
    @Test
    @DisplayName("강의평가 정지")
    void 강의평가_정지() {
        EvaluatePostRestrictForm evaluatePostRestrictForm = new EvaluatePostRestrictForm(
                1L,
                90L,
                "그냥",
                "90일정지"
        );
        executePostRequestWithAuthorizationResultActions(
                "/v2/admin/evaluate-posts/restrict",
                evaluatePostRestrictForm
        ).andExpect(status().isOk());
    }
}