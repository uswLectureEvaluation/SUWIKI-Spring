package usw.suwiki.domain.user.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface CustomUserRepository {

    void modifyRestricted(long targetUserIdx, boolean flag);

    void addRestrictedCount(long targetUserIdx);

    void addPoint(long targetUserIdx, int amount);

    void addWrittenExam(long targetUserIdx);
    void addWrittenEvaluate(long targetUserIdx);

    void subtractPoint(long targetUserIdx, int amount);

    void subtractWrittenExam(long targetUserIdx);
    void subtractWrittenEvaluate(long targetUserIdx);

    void modifyUpdatedAt(long targetUserIdx);

    void modifyViewExamCount(long targetUserIdx);

    void emailAuthedUser(long targetUserIdx);
}
