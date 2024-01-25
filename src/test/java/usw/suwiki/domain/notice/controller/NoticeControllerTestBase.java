package usw.suwiki.domain.notice.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Connection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.ResultActions;
import usw.suwiki.domain.notice.controller.dto.NoticeSaveOrUpdateDto;
import usw.suwiki.global.IntegrationTestBase;

class NoticeControllerTestBase extends IntegrationTestBase {

    @BeforeAll
    public void init() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-user.sql"));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-notice.sql"));
        }
    }

    @Test
    void 공지사항_모두_불러오기() throws Exception {
        ResultActions resultActions = executeGetRequestWithAuthorizationResultActions("/notice/all");

        final Long noticeId = 1L;
        final int RESPONSE_DATA_SIZE = 1;

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(RESPONSE_DATA_SIZE)))
                .andExpect(jsonPath("$.data[0].id").value(noticeId));
    }

    @Test
    void 공지사항_작성하기() throws Exception {
        NoticeSaveOrUpdateDto requestBody = new NoticeSaveOrUpdateDto(
                "testTitle",
                "testContent"
        );

        ResultActions resultActions = executePostRequestWithAuthorizationResultActions(
                "/notice/",
                requestBody
        );

        resultActions
                .andExpect(status().isOk());
    }


    @Test
    void 공지사항_상세_불러오기() throws Exception {
        final Long noticeId = 1L;

        ResultActions resultActions = executeGetRequestWithParameterResultActions(
                "/notice/all",
                "noticeId",
                String.valueOf(noticeId)
        );

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(noticeId));
    }
}