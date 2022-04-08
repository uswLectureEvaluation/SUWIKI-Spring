package usw.suwiki.repository.refreshToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usw.suwiki.domain.refreshToken.RefreshToken;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByPayload(String payload);

    Optional<RefreshToken> findByUserId(Long id);

    //페이로드 업데이트
    @Modifying
    @Query(value = "UPDATE RefreshToken Set payload = :newRefreshToken WHERE user_idx = :id")
    void updatePayload(@Param("newRefreshToken")String newRefreshToken, @Param("id")Long id);
}
