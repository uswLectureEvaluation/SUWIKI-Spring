package usw.suwiki.statistics.log;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.util.Optional;

interface ApiLoggerRepository extends CrudRepository<ApiLogger, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ApiLogger> findByCallDate(LocalDate callDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    ApiLogger save(ApiLogger apiLogger);
}
