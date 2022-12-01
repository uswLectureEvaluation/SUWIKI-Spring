package usw.suwiki.domain.blacklistdomain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.blacklistdomain.entity.BlacklistDomain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface BlacklistRepository extends JpaRepository<BlacklistDomain, Long> {

    List<BlacklistDomain> findByExpiredAtBefore(LocalDateTime targetTime);

    @Query(value = "SELECT * FROM blacklist_domain WHERE user_idx = :userIdx", nativeQuery = true)
    Optional<BlacklistDomain> findByUserId(@Param("userIdx") Long userIdx);

    @Query(value = "SELECT * FROM blacklist_domain WHERE user_idx = :userIdx", nativeQuery = true)
    List<BlacklistDomain> findByUserIdx(@Param("userIdx") Long userIdx);

    @Query(value = "SELECT * FROM blacklist_domain", nativeQuery = true)
    List<BlacklistDomain> findAllBlacklist();

    void deleteByUserIdx(Long id);
}