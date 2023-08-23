package usw.suwiki.controller.confirmationtoken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.BaseIntegrationTest;
import usw.suwiki.domain.confirmationtoken.ConfirmationToken;
import usw.suwiki.domain.confirmationtoken.service.ConfirmationTokenCRUDService;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ConfirmationTokenControllerTest extends BaseIntegrationTest {

    @Autowired
    ConfirmationTokenCRUDService confirmationTokenCRUDService;

    @BeforeEach
    public void setup() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(
                    conn,
                    new ClassPathResource("/data/insert-user.sql")
            );
            ScriptUtils.executeSqlScript(
                    conn,
                    new ClassPathResource("/data/insert-confirmationtoken.sql")
            );
        }
    }

    @Test
    public void testMethod() {
        List<ConfirmationToken> confirmationTokens =
                confirmationTokenCRUDService.loadNotConfirmedTokens(LocalDateTime.now());

        System.out.println(confirmationTokens.size());
    }
}
