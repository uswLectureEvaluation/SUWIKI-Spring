package usw.suwiki.domain.blacklistdomain.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import usw.suwiki.BaseIntegrationTest;
import usw.suwiki.global.jwt.JwtAgent;

import java.sql.Connection;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class BlacklistDomainControllerV2Test extends BaseIntegrationTest {

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
    @DisplayName("블랙리스트 사유 불러오기")
    void 블랙리스트_사유_불러오기() {
        buildGetRequestWithAuthorizationResultActions("/v2/blacklist/logs")
                .andExpect(status().isOk());
    }
}