package usw.suwiki.domain.user.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface CustomUserRepository {

    void updateRestricted(long targetUserIdx, boolean flag);

    void updateRestrictedCount(long targetUserIdx, int newRestrictedCount);

    void updatePoint(long targetUserIdx, int newAddedPoint);

    void updateWrittenExamCount(long targetUserIdx, int newWrittenExam);

    void updateWrittenEvaluateCount(long targetUserIdx, int newWrittenEvaluate);

    void updateUpdatedAt(long targetUserIdx);

    void updateViewExamCount(long targetUserIdx, int newViewExamCount);

    void updateUserEmailAuthStatus(long targetUserIdx);
}
