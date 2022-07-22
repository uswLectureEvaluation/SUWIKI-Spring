package usw.suwiki.domain.userIsolation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface UserIsolationRepository extends JpaRepository<UserIsolation, Long> {

    Optional<UserIsolation> findByUserIdx(Long userIdx);

    Optional<UserIsolation> findByLoginId(String loginId);

    Optional<UserIsolation> findByEmail(String email);

    Optional<UserIsolation> deleteByLoginId(String loginId);

    List<UserIsolation> findByRequestedQuitDateBefore(LocalDateTime localDateTime);

    List<UserIsolation> findByLastLoginBefore(LocalDateTime localDateTime);

    @Modifying(clearAutomatically = true)
    @Query(value = "INSERT INTO user_isolation " +
            "(user_idx, login_id, password, email)" +
            "SELECT id, login_id, password, email FROM user WHERE id = :id", nativeQuery = true)
    void convertSleepingUser(@Param("id") Long id);

}