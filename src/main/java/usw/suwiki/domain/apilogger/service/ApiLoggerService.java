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

    private final String lecturePostsOption = "lecture";
    private final String evaluatePostsOption = "evaluatePosts";
    private final String examPostsOption = "examPosts";
    private final String userOption = "user";
    private final String noticeOption = "notice";

    @Transactional
    public void logApi(LocalDate today, Long currentProcessTime, String option) {
        Optional<ApiLogger> apiLogger = apiLoggerRepository.findByCallDate(today);
        if (apiLogger.isEmpty()) {
            apiLoggerRepository.save(makeNewApiStatistics(today, currentProcessTime, option));
            return;
        }
        apiLoggerRepository.save(makeOldApiStatistics(apiLogger.get(), currentProcessTime, option));
    }

    private ApiLogger makeNewApiStatistics(
        LocalDate today, Long currentProcessTime, String option
    ) {
        ApiLogger newApiLogger = new ApiLogger();
        if (option.equals(lecturePostsOption)) {
            newApiLogger.saveNewLectureStatistics(today, currentProcessTime);
        } else if (option.equals(evaluatePostsOption)) {
            newApiLogger.saveNewEvaluatePostsStatistics(today, currentProcessTime);
        } else if (option.equals(examPostsOption)) {
            newApiLogger.saveNewExamPostsStatistics(today, currentProcessTime);
        } else if (option.equals(userOption)) {
            newApiLogger.saveNewUserStatistics(today, currentProcessTime);
        } else if (option.equals(noticeOption)) {
            newApiLogger.saveNewNoticeStatistics(today, currentProcessTime);
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