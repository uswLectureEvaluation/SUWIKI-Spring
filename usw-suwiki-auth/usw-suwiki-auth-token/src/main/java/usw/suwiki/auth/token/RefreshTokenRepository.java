package usw.suwiki.auth.token;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByPayload(String payload);

    Optional<RefreshToken> findByUserIdx(Long userIdx);

    void deleteByUserIdx(Long userIdx);
}
