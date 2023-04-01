package usw.suwiki.domain.apilogger.repository;

import com.usw.sugo.domain.apilogger.ApiLogger;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface ApiLoggerRepository extends CrudRepository<ApiLogger, Long> {

    Optional<ApiLogger> findByCallDate(LocalDate callDate);

}
