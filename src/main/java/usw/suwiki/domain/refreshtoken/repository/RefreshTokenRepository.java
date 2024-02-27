package usw.suwiki.domain.refreshtoken.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usw.suwiki.domain.refreshtoken.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByPayload(String payload);

    Optional<RefreshToken> findByUserIdx(Long userIdx);

    void deleteByUserIdx(Long userIdx);

}
