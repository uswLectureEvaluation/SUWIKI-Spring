package usw.suwiki.repository.emailToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.emailToken.ConfirmationToken;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    Optional<ConfirmationToken> findByToken(String token);


    //인증 시각 기록
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ConfirmationToken SET confirmedAt = :confirmedAt WHERE token = :token")
    void updateConfirmedAt(@Param("token") String token, @Param("confirmedAt") LocalDateTime confirmedAt);

    //유저 인덱스로 토큰 삭제
    @Modifying(clearAutomatically = true)
    @Query("DELETE from ConfirmationToken where id = :id")
    void deleteAllByIdInQuery(@Param("id") Long tokenId);

    //토큰 정보로 토큰 삭제
    @Modifying(clearAutomatically = true)
    @Query("DELETE from ConfirmationToken WHERE token = :token")
    void deleteAllByTokenInQuery(@Param("token") String token);

    //로그인 시 이메일 인증하지 않은 사용자 필터링
    @Query(value = "SELECT * FROM confirmation_token WHERE user_idx = :userIdx AND confirmed_at IS NOT NULL", nativeQuery = true)
    Optional<ConfirmationToken> isUserConfirmed(@Param("userIdx") Long userIdx);

}
