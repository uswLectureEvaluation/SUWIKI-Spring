package usw.suwiki.domain.apilogger.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import usw.suwiki.domain.apilogger.ApiLogger;

public interface ApiLoggerRepository extends CrudRepository<ApiLogger, Long> {

    Optional<ApiLogger> findByCallDate(LocalDate callDate);

}
