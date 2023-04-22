package usw.suwiki.domain.confirmationtoken.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.confirmationtoken.entity.ConfirmationToken;

@Repository
@Transactional(readOnly = true)
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    Optional<ConfirmationToken> findByToken(String token);

    Optional<ConfirmationToken> findByUserIdx(Long userIdx);

    void deleteByUserIdx(Long userIdx);

    //토큰 정보로 토큰 삭제
    @Modifying(clearAutomatically = true)
    @Query("DELETE from ConfirmationToken WHERE token = :token")
    void deleteAllByTokenInQuery(@Param("token") String token);


    @Query(value = "SELECT * FROM confirmation_token WHERE confirmed_at IS NULL AND expires_at < :targetTime", nativeQuery = true)
    List<ConfirmationToken> isUserConfirmed(@Param("targetTime") LocalDateTime targetTime);

}
