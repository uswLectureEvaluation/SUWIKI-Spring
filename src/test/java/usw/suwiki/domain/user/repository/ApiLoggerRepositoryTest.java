package usw.suwiki.domain.user.repository;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import usw.suwiki.domain.apilogger.ApiLogger;
import usw.suwiki.domain.apilogger.repository.ApiLoggerRepository;
import usw.suwiki.domain.apilogger.service.ApiLoggerService;

@SpringBootTest
public class ApiLoggerRepositoryTest {

    @Autowired
    ApiLoggerRepository apiLoggerRepository;

    @Autowired
    ApiLoggerService apiLoggerService;

    @Test
    public void test() {
        System.out.println(apiLoggerRepository.save(ApiLogger.builder()
            .callDate(LocalDate.now())
            .build()));
    }
}
