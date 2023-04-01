package usw.suwiki.domain.apilogger.service;

import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.apilogger.ApiLogger;
import usw.suwiki.domain.apilogger.repository.ApiLoggerRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApiLoggerService {

    private final ApiLoggerRepository apiLoggerRepository;

    @Transactional
    public void logApi(LocalDate today, Long currentProcessTime) {
        Optional<ApiLogger> apiLogger = apiLoggerRepository.findByCallDate(today);

        if (apiLogger.isEmpty()) {
            ApiLogger newTodayApiLogger = ApiLogger.builder()
                .callTime(1L)
                .callDate(today)
                .processAvg(currentProcessTime)
                .build();
            apiLoggerRepository.save(newTodayApiLogger);
            return;
        }
        apiLogger.get().calculateProcessAvg(currentProcessTime);
        apiLoggerRepository.save(apiLogger.get());
    }
}
