package usw.suwiki.domain.refreshtoken.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usw.suwiki.domain.refreshtoken.RefreshToken;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByPayload(String payload);

    Optional<RefreshToken> findByUserIdx(Long userIdx);

    void deleteByUserIdx(Long userIdx);

}
