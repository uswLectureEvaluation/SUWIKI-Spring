package usw.suwiki.domain.apilogger.service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.apilogger.ApiLogger;
import usw.suwiki.domain.apilogger.repository.ApiLoggerRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ApiLoggerService {

    private final ApiLoggerRepository apiLoggerRepository;
    private final String lecturePostsOption = "lecture";
    private final String evaluatePostsOption = "evaluatePosts";
    private final String examPostsOption = "examPosts";
    private final String userOption = "user";
    private final String noticeOption = "notice";

    @Transactional
    public void logApi(LocalDate today, Long currentProcessTime, String option) {
        Optional<ApiLogger> apiLogger = apiLoggerRepository.findByCallDate(today);
        if (apiLogger.isEmpty()) {
            try {
                apiLoggerRepository.save(makeNewApiStatistics(today, currentProcessTime, option));
            } catch (DataIntegrityViolationException exception) {
                log.error("Try to Create Duplicated Unique Key Exception message : {}", exception.getMessage());
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
        if (option.equals(lecturePostsOption)) {
            newApiLogger = newApiLogger.saveNewLectureStatistics(today, currentProcessTime);
        } else if (option.equals(evaluatePostsOption)) {
            newApiLogger = newApiLogger.saveNewEvaluatePostsStatistics(today, currentProcessTime);
        } else if (option.equals(examPostsOption)) {
            newApiLogger = newApiLogger.saveNewExamPostsStatistics(today, currentProcessTime);
        } else if (option.equals(userOption)) {
            newApiLogger = newApiLogger.saveNewUserStatistics(today, currentProcessTime);
        } else if (option.equals(noticeOption)) {
            newApiLogger = newApiLogger.saveNewNoticeStatistics(today, currentProcessTime);
        }
        return newApiLogger;
    }

    private ApiLogger makeOldApiStatistics(
        ApiLogger apiLogger, Long currentProcessTime, String option
    ) {
        if (option.equals(lecturePostsOption)) {
            apiLogger.calculateLectureApiStatistics(currentProcessTime);
        } else if (option.equals(evaluatePostsOption)) {
            apiLogger.calculateEvaluatePostsApiStatistics(currentProcessTime);
        } else if (option.equals(examPostsOption)) {
            apiLogger.calculateExamPostsStatistics(currentProcessTime);
        } else if (option.equals(userOption)) {
            apiLogger.calculateUserApiStatistics(currentProcessTime);
        } else if (option.equals(noticeOption)) {
            apiLogger.calculateNoticeApiStatistics(currentProcessTime);
        }
        return apiLogger;
    }
}