package usw.suwiki.domain.user.user.repository;

import org.springframework.stereotype.Repository;
import usw.suwiki.domain.user.userIsolation.entity.UserIsolation;

import java.time.LocalDateTime;

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

    void updateLastLogin(LocalDateTime now, Long userIdx);

    void updatePassword(String resetPassword, String loginId);

    void applyUserSoftDelete(Long id);

    void unapplyUserSoftDelete(Long id, UserIsolation userIsolation);
}
