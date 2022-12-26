package usw.suwiki.domain.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.entity.Role;

import java.time.LocalDateTime;

import static usw.suwiki.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
@Transactional
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
    public void updatePoint(long targetUserIdx, int newAddedPoint) {
        queryFactory
                .update(user)
                .set(user.point, newAddedPoint)
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
}
