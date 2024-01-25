package usw.suwiki.global;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.global.exception.BuildResultActionsException;
import usw.suwiki.global.jwt.JwtAgent;

@SpringBootTest
@Disabled
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IntegrationTestBase {
    @MockBean
    JwtAgent jwtAgent;
    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected EntityManagerFactory entityManagerFactory;
    @Autowired
    protected DataSource dataSource;

    public ResultActions executeGetRequestWithAuthorizationResultActions(
            final String url
    ) {
        String authorization = "authorization";
        when(jwtAgent.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);
        when(jwtAgent.getId(authorization)).thenReturn(14L);
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


    public ResultActions executePostRequestResultActions(
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

    public ResultActions executePostRequestWithAuthorizationResultActions(
            final String url,
            final Object dto
    ) {
        String authorization = "authorization";
        when(jwtAgent.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);
        when(jwtAgent.getId(authorization)).thenReturn(14L);
        when(jwtAgent.getUserRole(authorization)).thenReturn("ADMIN");
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

    public ResultActions executePostRequestWithAuthorizationNotContainedBodyResultActions(
            final String url
    ) {
        String authorization = "authorization";
        when(jwtAgent.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);
        when(jwtAgent.getId(authorization)).thenReturn(14L);
        when(jwtAgent.getUserRole(authorization)).thenReturn("ADMIN");
        try {
            return mvc.perform(
                            post(url)
                                    .header("Authorization", authorization)
                                    .contentType(APPLICATION_JSON)
                                    .accept(APPLICATION_JSON))
                    .andDo(print());
        } catch (Exception e) {
            throw new BuildResultActionsException(e.getCause());
        }
    }

    public ResultActions executePatchRequestWithAuthorizationResultActions(
            final String url,
            final Object dto
    ) {
        String authorization = "authorization";
        when(jwtAgent.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);
        when(jwtAgent.getId(authorization)).thenReturn(14L);
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

    public ResultActions executeGetRequestWithParameterResultActions(
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

    public ResultActions executeGetRequestWithAuthorizationParameterResultActions(
            final String url,
            final String parameterName,
            final String value
    ) {
        String authorization = "authorization";
        when(jwtAgent.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);
        when(jwtAgent.getId(authorization)).thenReturn(14L);
        when(jwtAgent.getUserRole(authorization)).thenReturn("ADMIN");

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

    public ResultActions executeGetRequestWithNotAuthorizedParameterResultActions(
            final String url,
            final String parameterName,
            final String value
    ) {
        String authorization = "authorization";
        when(jwtAgent.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);
        when(jwtAgent.getId(authorization)).thenReturn(14L);
        when(jwtAgent.getUserRole(authorization)).thenReturn("NOT_AUTH");

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

    public ResultActions executeDeleteRequestWithAuthorizationResultActions(
            final String url,
            final Object dto
    ) {
        String authorization = "authorization";
        when(jwtAgent.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);
        when(jwtAgent.getId(authorization)).thenReturn(14L);

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