package usw.suwiki.statistics.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApiLoggerService {

    private final ApiLoggerRepository apiLoggerRepository;
    private final String lecturePostsOption = "lecture";
    private final String evaluatePostsOption = "evaluatePosts";
    private final String examPostsOption = "examPosts";
    private final String userOption = "user";
    private final String noticeOption = "notice";

    @Async
    @Transactional
    public void logApi(LocalDate today, Long currentProcessTime, String option) {
        Optional<ApiLogger> apiLogger = apiLoggerRepository.findByCallDate(today);
        if (apiLogger.isEmpty()) {
            try {
                apiLoggerRepository.save(makeNewApiStatistics(today, currentProcessTime, option));
            } catch (DataIntegrityViolationException exception) {
                log.error("Try to Create Duplicated Unique Key Exception message : {}",
                    exception.getMessage());
                logApi(today, currentProcessTime, option);
            }
            return;
        }
        apiLoggerRepository.save(makeOldApiStatistics(apiLogger.get(), currentProcessTime, option));
    }

    private ApiLogger makeNewApiStatistics(
        LocalDate today, Long currentProcessTime, String option
    ) {
        ApiLogger newApiLogger = new ApiLogger();
        newApiLogger = switch (option) {
            case lecturePostsOption -> newApiLogger.saveNewLectureStatistics(today, currentProcessTime);
            case evaluatePostsOption -> newApiLogger.saveNewEvaluatePostsStatistics(today, currentProcessTime);
            case examPostsOption -> newApiLogger.saveNewExamPostsStatistics(today, currentProcessTime);
            case userOption -> newApiLogger.saveNewUserStatistics(today, currentProcessTime);
            case noticeOption -> newApiLogger.saveNewNoticeStatistics(today, currentProcessTime);
            default -> newApiLogger;
        };
        return newApiLogger;
    }

    private ApiLogger makeOldApiStatistics(
        ApiLogger apiLogger, Long currentProcessTime, String option
    ) {
        switch (option) {
            case lecturePostsOption -> apiLogger.calculateLectureApiStatistics(currentProcessTime);
            case evaluatePostsOption -> apiLogger.calculateEvaluatePostsApiStatistics(currentProcessTime);
            case examPostsOption -> apiLogger.calculateExamPostsStatistics(currentProcessTime);
            case userOption -> apiLogger.calculateUserApiStatistics(currentProcessTime);
            case noticeOption -> apiLogger.calculateNoticeApiStatistics(currentProcessTime);
        }
        return apiLogger;
    }
}
