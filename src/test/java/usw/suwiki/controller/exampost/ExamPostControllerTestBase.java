package usw.suwiki.controller.exampost;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.ResultActions;
import usw.suwiki.IntegrationTestBase;

import java.sql.Connection;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExamPostControllerTestBase extends IntegrationTestBase {

    @BeforeAll
    public void init() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-user.sql"));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-lecture.sql"));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-exampost.sql"));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-viewexam.sql"));
        }
    }

    @Test
    void 시험정보_불러오기_권한_있는사람() throws Exception {
        ResultActions resultActions = executeGetRequestWithAuthorizationParameterResultActions(
                "/exam-posts/",
                "lectureId",
                String.valueOf(1)
        );

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canRead").value(Boolean.TRUE))
                .andExpect(jsonPath("$.examDataExist").value(Boolean.TRUE));
    }

    @Test
    void 시험정보_불러오기_권한_없는사람() throws Exception {
        ResultActions resultActions = executeGetRequestWithNotAuthorizedParameterResultActions(
                "/evaluate-posts/",
                "lectureId",
                String.valueOf(1)
        );
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canRead").value(Boolean.FALSE))
                .andExpect(jsonPath("$.examDataExist").value(Boolean.TRUE))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void 시험정보_중복구매_예외_테스트() throws Exception {
        ResultActions resultActions = executePostRequestWithAuthorizationNotContainedBodyResultActions(
                "/exam-posts/purchase/?lectureId=1"
        );

        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    void 시험정보_존재하지않을때_정상동작_테스트() throws Exception {
        ResultActions resultActions = executeGetRequestWithAuthorizationParameterResultActions(
                "/exam-posts/purchase/?lectureId=1",
                "lectureId",
                String.valueOf(2)
        );

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.examDataExist").value(Boolean.FALSE));
    }
}