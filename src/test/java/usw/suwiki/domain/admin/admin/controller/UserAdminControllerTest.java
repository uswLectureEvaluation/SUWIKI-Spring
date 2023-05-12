package usw.suwiki.domain.admin.admin.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.ResultActions;
import usw.suwiki.BaseIntegrationTest;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.EvaluatePostRestrictForm;
import usw.suwiki.global.jwt.JwtAgent;
import usw.suwiki.global.util.BuildResultActionsException;

import java.sql.Connection;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserAdminControllerTest extends BaseIntegrationTest {

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

    @SneakyThrows(Exception.class)
    @Test
    @DisplayName("아이디 중복확인 - 중복일 시")
    void 강의평가_정지() {
        EvaluatePostRestrictForm evaluatePostRestrictForm = new EvaluatePostRestrictForm(
                1L,
                90L,
                "그냥",
                "90일정지"
        );
        buildPostRequestWithAuthorizationResultActions(
                "/admin/evaluate-posts/restrict",
                evaluatePostRestrictForm
        ).andExpect(status().isOk());
    }

    private ResultActions buildPostRequestWithAuthorizationResultActions(
            final String url,
            final Object dto
    ) {
        String authorization = "authorization";
        when(jwtAgent.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);
        when(jwtAgent.getId(authorization)).thenReturn(1L);
        try {
            return mvc.perform(
                            post(url)
                                    .header("Authorization", authorization)
                                    .content(objectMapper.writeValueAsString(dto))
                                    .contentType(APPLICATION_JSON)
                                    .accept(APPLICATION_JSON))
                    .andDo(print());
        } catch (Exception e) {
            throw new BuildResultActionsException(e.getCause());
        }
    }
}