package usw.suwiki.domain.confirmationtoken.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import usw.suwiki.domain.confirmationtoken.ConfirmationToken;

import java.time.LocalDateTime;
import java.util.List;

import static usw.suwiki.domain.confirmationtoken.QConfirmationToken.confirmationToken;


@Repository
@RequiredArgsConstructor
public class ConfirmationTokenQueryDslRepositoryImpl implements ConfirmationTokenQueryDslRepository {

    private final JPAQueryFactory query;

    @Override
    public List<ConfirmationToken> loadNotConfirmedTokensByExpiresAtIsNull(LocalDateTime localDateTime) {
        return query
                .selectFrom(confirmationToken)
                .where(confirmationToken.expiresAt.before(localDateTime))
                .where(confirmationToken.confirmedAt.isNull())
                .fetch();
    }
}
