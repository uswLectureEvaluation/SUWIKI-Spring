package usw.suwiki;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import usw.suwiki.global.jwt.JwtAgent;
import usw.suwiki.global.util.BuildResultActionsException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@Disabled
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseIntegrationTest {
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
    protected EntityManager entityManager;

    public ResultActions buildGetRequestWithAuthorizationResultActions(
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


    public ResultActions buildPostRequestResultActions(
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

    public ResultActions buildPostRequestWithAuthorizationResultActions(
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

    public ResultActions buildPatchRequestWithAuthorizationResultActions(
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

    public ResultActions buildGetRequestWithParameterResultActions(
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

    public ResultActions buildDeleteRequestWithAuthorizationResultActions(
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