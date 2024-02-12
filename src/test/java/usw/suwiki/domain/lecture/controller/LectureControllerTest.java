package usw.suwiki.domain.lecture.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Connection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.ResultActions;
import usw.suwiki.global.IntegrationTestBase;
import usw.suwiki.global.jwt.JwtAgent;

class LectureControllerTest extends IntegrationTestBase {

    @MockBean
    JwtAgent jwtAgent;

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
    void 전체강의불러오기_강의평가개수우선_그다음_시간순_페이징_10개씩() throws Exception {
        ResultActions resultActions = executeGetRequestWithAuthorizationResultActions("/lecture/all");

        final int RESPONSE_DATA_SIZE = 3;
        final int RESPONSE_FIRST_ID = 2;
        final int RESPONSE_SECOND_ID = 1;
        final int RESPONSE_THIRD_ID = 3;

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(RESPONSE_DATA_SIZE)))
                .andExpect(jsonPath("$.data[0].id").value(RESPONSE_FIRST_ID))
                .andExpect(jsonPath("$.data[1].id").value(RESPONSE_SECOND_ID))
                .andExpect(jsonPath("$.data[2].id").value(RESPONSE_THIRD_ID));
    }

    @Test
    void 전체강의불러오기_Best강의순_페이징_10개씩() throws Exception {
        ResultActions resultActions = executeGetRequestWithAuthorizationResultActions(
                "/lecture/all/?option=lectureTotalAvg"
        );

        final int RESPONSE_DATA_SIZE = 3;
        final int RESPONSE_FIRST_ID = 1;
        final int RESPONSE_SECOND_ID = 2;
        final int RESPONSE_THIRD_ID = 3;

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(RESPONSE_DATA_SIZE)))
                .andExpect(jsonPath("$.data[0].id").value(RESPONSE_FIRST_ID))
                .andExpect(jsonPath("$.data[1].id").value(RESPONSE_SECOND_ID))
                .andExpect(jsonPath("$.data[2].id").value(RESPONSE_THIRD_ID));
    }

    @Test
    void ID로_특정강의_불러오기() throws Exception {
        ResultActions resultActions = executeGetRequestWithAuthorizationParameterResultActions(
                "/lecture/",
                "lectureId",
                String.valueOf(1)
        );

        resultActions
                .andExpect(status().isOk());
    }

    @Test
    void 키워드로_강의_검색하기() throws Exception {
        ResultActions resultActions = executeGetRequestWithAuthorizationParameterResultActions(
                "/lecture/search",
                "searchValue",
                String.valueOf(1)
        );

        final int RESPONSE_DATA_SIZE = 1;
        final int RESPONSE_ID = 1;

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(RESPONSE_DATA_SIZE)))
                .andExpect(jsonPath("$.data[0].id").value(RESPONSE_ID));
    }


    // TODO test: 강의 시간표 리스트 조회 성공 - 응답 리스트 개수가 요청 size보다 클 수 있음
}