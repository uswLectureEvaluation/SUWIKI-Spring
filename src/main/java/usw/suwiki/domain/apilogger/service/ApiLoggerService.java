package usw.suwiki.domain.apilogger.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.apilogger.ApiLogger;
import usw.suwiki.domain.apilogger.repository.ApiLoggerRepository;

import java.time.LocalDate;
import java.util.Optional;

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

    @Async
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
        switch (option) {
            case lecturePostsOption:
                newApiLogger = newApiLogger.saveNewLectureStatistics(today, currentProcessTime);
                break;
            case evaluatePostsOption:
                newApiLogger = newApiLogger.saveNewEvaluatePostsStatistics(today, currentProcessTime);
                break;
            case examPostsOption:
                newApiLogger = newApiLogger.saveNewExamPostsStatistics(today, currentProcessTime);
                break;
            case userOption:
                newApiLogger = newApiLogger.saveNewUserStatistics(today, currentProcessTime);
                break;
            case noticeOption:
                newApiLogger = newApiLogger.saveNewNoticeStatistics(today, currentProcessTime);
                break;
        }
        return newApiLogger;
    }

    private ApiLogger makeOldApiStatistics(
            ApiLogger apiLogger, Long currentProcessTime, String option
    ) {
        switch (option) {
            case lecturePostsOption:
                apiLogger.calculateLectureApiStatistics(currentProcessTime);
                break;
            case evaluatePostsOption:
                apiLogger.calculateEvaluatePostsApiStatistics(currentProcessTime);
                break;
            case examPostsOption:
                apiLogger.calculateExamPostsStatistics(currentProcessTime);
                break;
            case userOption:
                apiLogger.calculateUserApiStatistics(currentProcessTime);
                break;
            case noticeOption:
                apiLogger.calculateNoticeApiStatistics(currentProcessTime);
                break;
        }
        return apiLogger;
    }
}