package usw.suwiki.domain.user.userIsolation.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class CustomUserIsolationRepositoryImpl implements CustomUserIsolationRepository {

    private final JPAQueryFactory queryFactory;
}
