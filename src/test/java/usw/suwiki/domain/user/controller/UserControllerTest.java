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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        buildPostRequestResultActions("/v2/user/loginId/check", checkLoginIdForm)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.overlap").value(TRUE));
    }

    @SneakyThrows(Exception.class)
    @Test
    @DisplayName("아이디 중복확인 - 중복이 아닐 시")
    void 아이디_중복확인_중복이_아닐_시() {
        CheckLoginIdForm checkLoginIdForm = new CheckLoginIdForm("user5");
        buildPostRequestResultActions("/v2/user/loginId/check", checkLoginIdForm)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.overlap").value(FALSE));
    }

    @SneakyThrows(Exception.class)
    @Test
    @DisplayName("이메일 중복확인 - 중복일 시")
    void 이메일_중복확인_중복일_시() {
        CheckEmailForm checkEmailForm = new CheckEmailForm("user1@suwon.ac.kr");
        buildPostRequestResultActions("/v2/user/email/check", checkEmailForm)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.overlap").value(TRUE));
    }

    @SneakyThrows(Exception.class)
    @Test
    @DisplayName("이메일 중복확인 - 중복이 아닐 시")
    void 이메일_중복확인_중복이_아닐_시() {
        CheckEmailForm checkEmailForm = new CheckEmailForm("user5@suwon.ac.kr");
        buildPostRequestResultActions("/v2/user/email/check", checkEmailForm)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.overlap").value(FALSE));
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

        buildPostRequestResultActions("/v2/user", joinForm)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(TRUE));
    }

    @SneakyThrows(Exception.class)
    @Test
    @DisplayName("이메일 인증 - 성공")
    void 이메일_인증_성공() {
        buildGetRequestWithParameterResultActions(
                "/v2/confirmation-token/verify",
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
                "/v2/user/inquiry-loginId",
                findIdForm
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(TRUE));
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
                "/v2/user/inquiry-password",
                findPasswordForm
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(TRUE));
    }

    @SneakyThrows(Exception.class)
    @Test
    @DisplayName("비밀번호 재설정 요청 - 성공")
    void 비밀번호_재설정_요청_성공() {
        EditMyPasswordForm editMyPasswordForm = new EditMyPasswordForm(
                "qwer1234!",
                "1q2w3e4r!"
        );

        buildPatchRequestWithAuthorizationResultActions(
                "/v2/user/password",
                editMyPasswordForm
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(TRUE));
    }

    @SneakyThrows(Exception.class)
    @Test
    @DisplayName("모바일 로그인 - 성공")
    void 로그인__성공() {
        LoginForm loginForm = new LoginForm(
                "user3",
                "qwer1234!"
        );

        buildPostRequestResultActions(
                "/v2/user/mobile-login",
                loginForm
        )
                .andExpect(status().isOk());
        //.andExpect(jsonPath("$.data.*").value(new HashMap<>()));
    }

    @SneakyThrows(Exception.class)
    @Test
    @DisplayName("웹 로그인 - 성공")
    void 웹_로그인_성공() {
        LoginForm loginForm = new LoginForm(
                "user3",
                "qwer1234!"
        );

        buildPostRequestResultActions(
                "/v2/user/web-login",
                loginForm
        )
                .andExpect(status().isOk());
        //.andExpect(jsonPath("$.data.*").value(new HashMap<>()));
    }

    @SneakyThrows(Exception.class)
    @Test
    @DisplayName("유저 정보 로드 - 성공")
    void 유저_정보_로드_성공() {

        buildGetRequestWithAuthorizationResultActions(
                "/v2/user"
        )
                .andExpect(status().isOk());
    }

    @SneakyThrows(Exception.class)
    @Test
    @DisplayName("유저 회원탈퇴 - 성공")
    void 유저_회원탈퇴() {

        UserQuitForm userQuitForm = new UserQuitForm(
                "user3",
                "qwer1234!"
        );

        buildDeleteRequestWithAuthorizationResultActions(
                "/v2/user/web-login",
                userQuitForm
        )
                .andExpect(status().isOk());
    }

    private ResultActions buildGetRequestWithAuthorizationResultActions(
            final String url
    ) {
        String authorization = "authorization";
        when(jwtAgent.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);
        when(jwtAgent.getId(authorization)).thenReturn(3L);
        try {
            return mvc.perform(
                            get(url)
                                    .header("Authorization", authorization)
                                    .contentType(APPLICATION_JSON)
                                    .accept(APPLICATION_JSON))
                    .andDo(print());
        } catch (Exception e) {
            throw new BuildResultActionsException(e.getCause());
        }
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
        when(jwtAgent.getId(authorization)).thenReturn(3L);
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

    private ResultActions buildPatchRequestWithAuthorizationResultActions(
            final String url,
            final Object dto
    ) {
        String authorization = "authorization";
        when(jwtAgent.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);
        when(jwtAgent.getId(authorization)).thenReturn(3L);
        try {
            return mvc.perform(
                            patch(url)
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

    private ResultActions buildDeleteRequestWithAuthorizationResultActions(
            final String url,
            final Object dto
    ) {
        String authorization = "authorization";
        when(jwtAgent.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);
        when(jwtAgent.getId(authorization)).thenReturn(3L);

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
}
