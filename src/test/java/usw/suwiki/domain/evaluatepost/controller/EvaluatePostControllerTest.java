package usw.suwiki.domain.evaluatepost.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Connection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.ResultActions;
import usw.suwiki.domain.evaluatepost.controller.dto.EvaluatePostSaveDto;
import usw.suwiki.global.IntegrationTestBase;
import usw.suwiki.global.jwt.JwtAgent;

class EvaluatePostControllerTest extends IntegrationTestBase {

    @MockBean
    JwtAgent jwtAgent;

    @BeforeAll
    public void init() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-user.sql"));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-lecture.sql"));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-evaluatepost.sql"));
        }
    }

    @Test
    void 이미_작성한유저_강의평가_불러오기() throws Exception {
        ResultActions resultActions = executeGetRequestWithAuthorizationParameterResultActions(
                "/evaluate-posts/",
                "lectureId",
                String.valueOf(1)
        );

        final Long postId = 1L;

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(postId))
                .andExpect(jsonPath("$.written").value(Boolean.TRUE));
    }

    @Test
    void 작성하지않은유저_강의평가_불러오기() throws Exception {
        ResultActions resultActions = executeGetRequestWithAuthorizationParameterResultActions(
                "/evaluate-posts/",
                "lectureId",
                String.valueOf(1)
        );

        final Long postId = 1L;

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(postId))
                .andExpect(jsonPath("$.written").value(Boolean.FALSE));
    }

    @Test
    void 강의평가_중첩_작성하기_예외_테스트() throws Exception {
        EvaluatePostSaveDto requestBody = EvaluatePostSaveDto.builder()
                .lectureName("testLecture")
                .selectedSemester("2022-1")
                .professor("testProfessor")
                .content("testContent")
                .satisfaction(5.0f)
                .honey(5.0f)
                .learning(5.0f)
                .homework(0)
                .team(0)
                .difficulty(0)
                .build();

        ResultActions resultActions = executePostRequestWithAuthorizationResultActions(
                "/evaluate-posts/?lectureId=1", requestBody
        );

        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    void 강의평가_작성하기() throws Exception {
        EvaluatePostSaveDto requestBody = EvaluatePostSaveDto.builder()
                .lectureName("testLecture")
                .selectedSemester("2022-1")
                .professor("testProfessor")
                .content("testContent")
                .satisfaction(5.0f)
                .honey(5.0f)
                .learning(5.0f)
                .homework(0)
                .team(0)
                .difficulty(0)
                .build();

        ResultActions resultActions = executePostRequestWithAuthorizationResultActions(
                "/evaluate-posts/?lectureId=2", requestBody
        );

        resultActions
                .andExpect(status().isOk());
    }

    @Test
    void 내가작성한_강의평가_조회하기() throws Exception {
        ResultActions resultActions = executeGetRequestWithAuthorizationResultActions(
                "/evaluate-posts/written"
        );
        resultActions
                .andExpect(status().isOk());
    }

}