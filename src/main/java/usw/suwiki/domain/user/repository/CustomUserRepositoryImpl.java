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
    public void modifyRestricted(long targetUserIdx, boolean flag) {
        queryFactory
                .update(user)
                .set(user.restricted, flag)
                .where(user.id.eq(targetUserIdx))
                .execute();
    }

    @Override
    public void addRestrictedCount(long targetUserIdx) {
        queryFactory
                .update(user)
                .set(user.restrictedCount, user.restrictedCount.add(1))
                .where(user.id.eq(targetUserIdx))
                .execute();
    }

    @Override
    public void addPoint(long targetUserIdx, int amount) {
        queryFactory
                .update(user)
                .set(user.point, user.point.add(amount))
                .where(user.id.eq(targetUserIdx))
                .execute();
    }

    @Override
    public void addWrittenExam(long targetUserIdx) {
        queryFactory
                .update(user)
                .set(user.writtenExam, user.writtenExam.add(1))
                .where(user.id.eq(targetUserIdx))
                .execute();
    }

    @Override
    public void addWrittenEvaluate(long targetUserIdx) {
        queryFactory
                .update(user)
                .set(user.writtenEvaluation, user.writtenEvaluation.add(1))
                .where(user.id.eq(targetUserIdx))
                .execute();
    }

    @Override
    public void subtractPoint(long targetUserIdx, int amount) {
        queryFactory
                .update(user)
                .set(user.point, user.point.subtract(20))
                .where(user.id.eq(targetUserIdx))
                .execute();
    }

    @Override
    public void subtractWrittenExam(long targetUserIdx) {
        queryFactory
                .update(user)
                .set(user.writtenExam, user.writtenExam.subtract(1))
                .where(user.id.eq(targetUserIdx))
                .execute();
    }

    @Override
    public void subtractWrittenEvaluate(long targetUserIdx) {
        queryFactory
                .update(user)
                .set(user.writtenEvaluation, user.writtenEvaluation.subtract(1))
                .where(user.id.eq(targetUserIdx))
                .execute();
    }

    @Override
    public void modifyUpdatedAt(long targetUserIdx) {
        queryFactory
                .update(user)
                .set(user.updatedAt, LocalDateTime.now())
                .where(user.id.eq(targetUserIdx))
                .execute();
    }

    @Override
    public void modifyViewExamCount(long targetUserIdx) {
        queryFactory
                .update(user)
                .set(user.viewExamCount, user.viewExamCount.add(1))
                .where(user.id.eq(targetUserIdx))
                .execute();
    }

    @Override
    public void emailAuthedUser(long targetUserIdx) {
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
