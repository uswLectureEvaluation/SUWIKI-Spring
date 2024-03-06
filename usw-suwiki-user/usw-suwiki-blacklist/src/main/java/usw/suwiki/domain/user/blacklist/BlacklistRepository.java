package usw.suwiki.domain.user.blacklist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BlacklistRepository extends JpaRepository<BlacklistDomain, Long> {

    Optional<BlacklistDomain> findByUserIdx(Long userIdx);

    List<BlacklistDomain> findByExpiredAtBefore(LocalDateTime targetTime);

    void deleteByUserIdx(Long userIdx);
}
