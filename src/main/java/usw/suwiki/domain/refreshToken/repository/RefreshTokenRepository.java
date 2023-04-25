package usw.suwiki.domain.refreshToken.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usw.suwiki.domain.refreshToken.entity.RefreshToken;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>, CustomRefreshTokenRepository {

    Optional<RefreshToken> findByPayload(String payload);
    Optional<RefreshToken> findByUserIdx(Long userIdx);

    void deleteByUserIdx(@Param("userIdx") Long userIdx);

}
