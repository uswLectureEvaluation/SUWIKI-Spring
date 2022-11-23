package usw.suwiki.domain.refreshToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByPayload(String payload);

    @Query(value = "SELECT payload FROM refresh_token WHERE user_idx = :id", nativeQuery = true)
    Optional<String> findPayLoadByUserIdx(@Param("id") Long id);

    //페이로드 업데이트
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE RefreshToken Set payload = :newRefreshToken WHERE user_idx = :id")
    void updatePayload(@Param("newRefreshToken") String newRefreshToken, @Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM refresh_token WHERE user_idx = :userIdx", nativeQuery = true)
    void deleteByUserIdx(@Param("userIdx") Long userIdx);
}
