package usw.suwiki.domain.apilogger.repository;

import java.time.LocalDate;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import usw.suwiki.domain.apilogger.ApiLogger;

public interface ApiLoggerRepository extends CrudRepository<ApiLogger, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ApiLogger> findByCallDate(LocalDate callDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    ApiLogger save(ApiLogger apiLogger);
}
