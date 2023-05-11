package usw.suwiki.domain.user.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.ResultActions;
import usw.suwiki.BaseIntegrationTest;
import usw.suwiki.domain.user.user.controller.dto.UserRequestDto.*;
import usw.suwiki.global.jwt.JwtAgent;
import usw.suwiki.global.util.BuildResultActionsException;

import java.sql.Connection;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends BaseIntegrationTest {

    @MockBean
    JwtAgent jwtAgent;

    @BeforeAll
    public void init() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-user.sql"));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-confirmationtoken.sql"));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-lecture.sql"));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-exampost.sql"));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-viewexam.sql"));
        }
    }

    @SneakyThrows(Exception.class)
    @Test
    @DisplayName("아이디 중복확인 - 중복일 시")
    void 아이디_중복확인_중복일_시() {
        CheckLoginIdForm checkLoginIdForm = new CheckLoginIdForm("user1");
        buildPostRequestResultActions("/user/check-id", checkLoginIdForm)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overlap").value(TRUE));
    }

    @SneakyThrows(Exception.class)
    @Test
    @DisplayName("아이디 중복확인 - 중복이 아닐 시")
    void 아이디_중복확인_중복이_아닐_시() {
        CheckLoginIdForm checkLoginIdForm = new CheckLoginIdForm("user3");
        buildPostRequestResultActions("/user/check-id", checkLoginIdForm)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overlap").value(FALSE));
    }

    @SneakyThrows(Exception.class)
    @Test
    @DisplayName("이메일 중복확인 - 중복일 시")
    void 이메일_중복확인_중복일_시() {
        CheckEmailForm checkEmailForm = new CheckEmailForm("user1@suwon.ac.kr");
        buildPostRequestResultActions("/user/check-email", checkEmailForm)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overlap").value(TRUE));
    }

    @SneakyThrows(Exception.class)
    @Test
    @DisplayName("이메일 중복확인 - 중복이 아닐 시")
    void 이메일_중복확인_중복이_아닐_시() {
        CheckEmailForm checkEmailForm = new CheckEmailForm("user3@suwon.ac.kr");
        buildPostRequestResultActions("/user/check-email", checkEmailForm)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overlap").value(FALSE));
    }

    @SneakyThrows(Exception.class)
    @Test
    @DisplayName("회원가입 - 성공")
    void 회원가입_성공() {
        final JoinForm joinForm = new JoinForm(
                "diger",
                "qwer1234!",
                "18018008@suwon.ac.kr"
        );

        buildPostRequestResultActions("/user/join", joinForm)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(TRUE));
    }

    @SneakyThrows(Exception.class)
    @Test
    @DisplayName("이메일 인증 - 성공")
    void 이메일_인증_성공() {
        buildGetRequestWithParameterResultActions(
                "/user/verify-email",
                "token",
                "payload"
        )
                .andExpect(status().isOk());
    }

    @SneakyThrows(Exception.class)
    @Test
    @DisplayName("아이디 찾기 요청 - 성공")
    void 아이디_찾기_요청_성공() {
        FindIdForm findIdForm = new FindIdForm("user1@suwon.ac.kr");
        buildPostRequestResultActions(
                "/user/find-id",
                findIdForm
        )
                .andExpect(status().isOk());
    }

    @SneakyThrows(Exception.class)
    @Test
    @DisplayName("비밀번호 찾기 요청 - 성공")
    void 비밀번호_찾기_요청_성공() {
        FindPasswordForm findPasswordForm = new FindPasswordForm(
                "user1",
                "user1@suwon.ac.kr"
        );

        buildPostRequestResultActions(
                "/user/find-pw",
                findPasswordForm
        )
                .andExpect(status().isOk());
    }

    @SneakyThrows(Exception.class)
    @Test
    @DisplayName("비밀번호 재설정 요청 - 성공")
    void 비밀번호_재설정_요청_성공() {
        EditMyPasswordForm editMyPasswordForm = new EditMyPasswordForm(
                "qwer1234!",
                "1q2w3e4r!"
        );

        buildPostRequestWithAuthorizationResultActions(
                "/user/reset-pw",
                editMyPasswordForm
        )
                .andExpect(status().isOk());
    }


    private ResultActions buildPostRequestResultActions(
            final String url,
            final Object dto
    ) {
        try {
            return mvc.perform(
                            post(url)
                                    .content(objectMapper.writeValueAsString(dto))
                                    .contentType(APPLICATION_JSON)
                                    .accept(APPLICATION_JSON))
                    .andDo(print());
        } catch (Exception e) {
            throw new BuildResultActionsException(e.getCause());
        }
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

    private ResultActions buildGetRequestWithParameterResultActions(
            final String url,
            final String parameterName,
            final String value
    ) {
        try {
            return mvc.perform(
                            get(url)
                                    .param(parameterName, value)
                                    .contentType(APPLICATION_JSON)
                                    .accept(APPLICATION_JSON))
                    .andDo(print());
        } catch (Exception e) {
            throw new BuildResultActionsException(e.getCause());
        }
    }
}

