package usw.suwiki.domain.apilogger.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import usw.suwiki.domain.apilogger.ApiLogger;

import javax.persistence.LockModeType;

/*
PESSIMISTIC_READ – 해당 리소스에 공유락을 겁니다. 타 트랜잭션에서 읽기는 가능하지만 쓰기는 불가능해집니다.
PESSIMISTIC_WRITE – 해당 리소스에 베타락을 겁니다. 타 트랜잭션에서는 읽기와 쓰기 모두 불가능해집니다. (DBMS 종류에 따라 상황이 달라질 수 있습니다)
 */

public interface ApiLoggerRepository extends CrudRepository<ApiLogger, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ApiLogger> findByCallDate(LocalDate callDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    ApiLogger save(ApiLogger apiLogger);
}
