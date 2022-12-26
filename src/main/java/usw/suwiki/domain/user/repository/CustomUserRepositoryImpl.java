package usw.suwiki.domain.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.entity.Role;
import usw.suwiki.domain.userIsolation.entity.UserIsolation;

import java.time.LocalDateTime;

import static usw.suwiki.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public void updateRestricted(long targetUserIdx, boolean flag) {
        queryFactory
                .update(user)
                .set(user.restricted, flag)
                .where(user.id.eq(targetUserIdx))
                .execute();
    }

    @Override
    public void updateRestrictedCount(long targetUserIdx, int newRestrictedCount) {
        queryFactory
                .update(user)
                .set(user.restrictedCount, newRestrictedCount)
                .where(user.id.eq(targetUserIdx))
                .execute();
    }

    @Override
    public void updatePoint(long targetUserIdx, int newPoint) {
        queryFactory
                .update(user)
                .set(user.point, newPoint)
                .where(user.id.eq(targetUserIdx))
                .execute();
    }

    @Override
    public void updateWrittenExamCount(long targetUserIdx, int newWrittenExamCount) {
        queryFactory
                .update(user)
                .set(user.writtenExam, newWrittenExamCount)
                .where(user.id.eq(targetUserIdx))
                .execute();
    }

    @Override
    public void updateWrittenEvaluateCount(long targetUserIdx, int newWrittenEvaluateCount) {
        queryFactory
                .update(user)
                .set(user.writtenEvaluation, newWrittenEvaluateCount)
                .where(user.id.eq(targetUserIdx))
                .execute();
    }

    @Override
    public void updateUpdatedAt(long targetUserIdx) {
        queryFactory
                .update(user)
                .set(user.updatedAt, LocalDateTime.now())
                .where(user.id.eq(targetUserIdx))
                .execute();
    }

    @Override
    public void updateViewExamCount(long targetUserIdx, int newViewExamCount) {
        queryFactory
                .update(user)
                .set(user.viewExamCount, user.viewExamCount.add(1))
                .where(user.id.eq(targetUserIdx))
                .execute();
    }

    @Override
    public void updateUserEmailAuthStatus(long targetUserIdx) {
        queryFactory
                .update(user)
                .set(user.restricted, false)
                .set(user.createdAt, LocalDateTime.now())
                .set(user.updatedAt, LocalDateTime.now())
                .set(user.role, Role.USER)
                .where(user.id.eq(targetUserIdx))
                .execute();
    }

    @Override
    public void updateLastLogin(LocalDateTime now, Long userIdx) {
        queryFactory
                .update(user)
                .set(user.lastLogin, now)
                .where(user.id.eq(userIdx))
                .execute();
    }

    @Override
    public void updatePassword(String newPassword, String loginId) {
        queryFactory
                .update(user)
                .set(user.password, newPassword)
                .where(user.loginId.eq(loginId))
                .execute();
    }

    @Override
    public void applyUserSoftDelete(Long userIdx) {
        queryFactory
                .update(user)
                .set(user.id, (Long) null)
                .set(user.loginId, (String) null)
                .set(user.password, (String) null)
                .set(user.email, (String) null)
                .set(user.lastLogin, (LocalDateTime) null)
                .set(user.requestedQuitDate, (LocalDateTime) null)
                .where(user.id.eq(userIdx))
                .execute();
    }

    @Override
    public void unapplyUserSoftDelete(Long userIdx, UserIsolation userIsolation) {
        queryFactory
                .update(user)
                .set(user.id, userIsolation.getUserIdx())
                .set(user.loginId, userIsolation.getLoginId())
                .set(user.password, userIsolation.getPassword())
                .set(user.email, userIsolation.getEmail())
                .set(user.lastLogin, userIsolation.getLastLogin())
                .set(user.requestedQuitDate, userIsolation.getRequestedQuitDate())
                .where(user.id.eq(userIdx))
                .execute();
    }
}
