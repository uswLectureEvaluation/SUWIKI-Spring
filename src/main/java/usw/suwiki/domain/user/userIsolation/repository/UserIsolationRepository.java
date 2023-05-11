package usw.suwiki.domain.user.userIsolation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usw.suwiki.domain.user.userIsolation.UserIsolation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserIsolationRepository extends JpaRepository<UserIsolation, Long> {

    Optional<UserIsolation> findByUserIdx(Long userIdx);

    Optional<UserIsolation> findByLoginId(String loginId);

    Optional<UserIsolation> findByEmail(String email);

    void deleteByLoginId(String loginId);

    void deleteByUserIdx(Long userIdx);

    List<UserIsolation> findByRequestedQuitDateBefore(LocalDateTime localDateTime);

    List<UserIsolation> findByLastLoginBefore(LocalDateTime localDateTime);

}