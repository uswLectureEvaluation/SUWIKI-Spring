package usw.suwiki.domain.refreshToken.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static usw.suwiki.domain.refreshToken.entity.QRefreshToken.refreshToken;

@Repository
@Transactional
@RequiredArgsConstructor
public class CustomRefreshTokenRepositoryImpl implements CustomRefreshTokenRepository {

    private final JPAQueryFactory queryFactory;

    public String loadPayloadByUserIdx(Long userIdx) {
        return queryFactory
                .select(refreshToken.payload)
                .from(refreshToken)
                .where(refreshToken.userIdx.eq(userIdx))
                .fetchOne();
    }

    @Override
    public void updatePayload(String newRefreshToken, Long userIdx) {
        queryFactory
                .update(refreshToken)
                .set(refreshToken.payload, newRefreshToken)
                .where(refreshToken.userIdx.eq(userIdx))
                .execute();
    }
}
