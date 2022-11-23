package usw.suwiki.domain.email.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.email.entity.ConfirmationToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    Optional<ConfirmationToken> findByToken(String token);

    void deleteByUserIdx(Long userIdx);

    //유저 인덱스로 해당 유저 이메일 인증 받았는지 검증
    @Query(value = "SELECT * FROM confirmation_token WHERE user_idx = :userIdx and confirmed_at IS NOT NULL", nativeQuery = true)
    Optional<ConfirmationToken> verifyUserEmailAuth(@Param("userIdx") Long userIdx);

    //인증 시각 기록
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ConfirmationToken SET confirmedAt = :confirmedAt WHERE token = :token")
    void updateConfirmedAt(@Param("token") String token, @Param("confirmedAt") LocalDateTime confirmedAt);

    //토큰 정보로 토큰 삭제
    @Modifying(clearAutomatically = true)
    @Query("DELETE from ConfirmationToken WHERE token = :token")
    void deleteAllByTokenInQuery(@Param("token") String token);


    @Query(value = "SELECT * FROM confirmation_token WHERE confirmed_at IS NULL AND expires_at < :targetTime", nativeQuery = true)
    List<ConfirmationToken> isUserConfirmed(@Param("targetTime") LocalDateTime targetTime);

}
