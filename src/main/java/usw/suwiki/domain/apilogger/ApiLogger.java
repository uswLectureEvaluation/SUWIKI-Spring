package usw.suwiki.domain.apilogger;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiLogger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private Long lectureApiCallTime;

    @Column
    private Long lectureApiProcessAvg;

    @Column
    private Long evaluatePostsApiCallTime;

    @Column
    private Long evaluatePostsApiProcessAvg;

    @Column
    private Long examPostsApiCallTime;

    @Column
    private Long examPostsApiProcessAvg;

    @Column
    private Long userApiCallTime;

    @Column
    private Long userApiProcessAvg;

    @Column
    private Long noticeApiCallTime;

    @Column
    private Long noticeApiProcessAvg;

    @Column
    private LocalDate callDate;

    public ApiLogger saveNewLectureStatistics(LocalDate today, Long currentProcessTime) {
        return ApiLogger.builder()
            .callDate(today)
            .lectureApiCallTime(1L)
            .lectureApiProcessAvg(currentProcessTime)
            .evaluatePostsApiCallTime(0L)
            .evaluatePostsApiProcessAvg(0L)
            .examPostsApiCallTime(0L)
            .examPostsApiProcessAvg(0L)
            .userApiCallTime(0L)
            .userApiProcessAvg(0L)
            .noticeApiCallTime(0L)
            .noticeApiProcessAvg(0L)
            .build();
    }

    public ApiLogger saveNewEvaluatePostsStatistics(LocalDate today, Long currentProcessTime) {
        return ApiLogger.builder()
            .callDate(today)
            .lectureApiCallTime(0L)
            .lectureApiProcessAvg(0L)
            .evaluatePostsApiCallTime(1L)
            .evaluatePostsApiProcessAvg(currentProcessTime)
            .examPostsApiCallTime(0L)
            .examPostsApiProcessAvg(0L)
            .userApiCallTime(0L)
            .userApiProcessAvg(0L)
            .noticeApiCallTime(0L)
            .noticeApiProcessAvg(0L)
            .build();
    }

    public ApiLogger saveNewExamPostsStatistics(LocalDate today, Long currentProcessTime) {
        return ApiLogger.builder()
            .callDate(today)
            .lectureApiCallTime(0L)
            .lectureApiProcessAvg(0L)
            .evaluatePostsApiCallTime(0L)
            .evaluatePostsApiProcessAvg(0L)
            .examPostsApiCallTime(1L)
            .examPostsApiProcessAvg(currentProcessTime)
            .userApiCallTime(0L)
            .userApiProcessAvg(0L)
            .noticeApiCallTime(0L)
            .noticeApiProcessAvg(0L)
            .build();
    }

    public ApiLogger saveNewUserStatistics(LocalDate today, Long currentProcessTime) {
        return ApiLogger.builder()
            .callDate(today)
            .lectureApiCallTime(0L)
            .lectureApiProcessAvg(0L)
            .evaluatePostsApiCallTime(0L)
            .evaluatePostsApiProcessAvg(0L)
            .examPostsApiCallTime(0L)
            .examPostsApiProcessAvg(0L)
            .userApiCallTime(1L)
            .userApiProcessAvg(currentProcessTime)
            .noticeApiCallTime(0L)
            .noticeApiProcessAvg(0L)
            .build();
    }

    public ApiLogger saveNewNoticeStatistics(LocalDate today, Long currentProcessTime) {
        return ApiLogger.builder()
            .callDate(today)
            .lectureApiCallTime(0L)
            .lectureApiProcessAvg(0L)
            .evaluatePostsApiCallTime(0L)
            .evaluatePostsApiProcessAvg(0L)
            .examPostsApiCallTime(0L)
            .examPostsApiProcessAvg(0L)
            .userApiCallTime(1L)
            .userApiProcessAvg(currentProcessTime)
            .noticeApiCallTime(1L)
            .noticeApiProcessAvg(currentProcessTime)
            .build();
    }

    public void calculateLectureApiStatistics(Long currentProcessTime) {
        this.lectureApiProcessAvg =
            (currentProcessTime + (lectureApiProcessAvg * lectureApiCallTime)) /
                (this.lectureApiCallTime + 1);
        this.lectureApiCallTime += 1;
    }

    public void calculateEvaluatePostsApiStatistics(Long currentProcessTime) {
        this.evaluatePostsApiProcessAvg =
            (currentProcessTime + (evaluatePostsApiProcessAvg * evaluatePostsApiCallTime)) /
                (this.evaluatePostsApiCallTime + 1);
        this.evaluatePostsApiCallTime += 1;
    }

    public void calculateExamPostsStatistics(Long currentProcessTime) {
        this.examPostsApiProcessAvg =
            (currentProcessTime + (examPostsApiProcessAvg * examPostsApiCallTime)) /
                (this.examPostsApiCallTime + 1);
        this.examPostsApiCallTime += 1;
    }

    public void calculateUserApiStatistics(Long currentProcessTime) {
        this.userApiProcessAvg =
            (currentProcessTime + (userApiProcessAvg * userApiCallTime)) /
                (this.userApiCallTime + 1);
        this.userApiCallTime += 1;
    }

    public void calculateNoticeApiStatistics(Long currentProcessTime) {
        this.noticeApiProcessAvg =
            (currentProcessTime + (noticeApiProcessAvg * noticeApiCallTime)) /
                (this.noticeApiCallTime + 1);
        this.noticeApiCallTime += 1;
    }
}
