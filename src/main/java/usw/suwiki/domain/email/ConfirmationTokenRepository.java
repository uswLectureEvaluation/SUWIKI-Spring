package usw.suwiki.domain.email;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    Optional<ConfirmationToken> findByToken(String token);
    
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

    //로그인 시 이메일 인증하지 않은 사용자 필터링
    @Query(value = "SELECT * FROM confirmation_token WHERE user_idx = :userIdx AND confirmed_at IS NOT NULL", nativeQuery = true)
    Optional<ConfirmationToken> isUserConfirmed(@Param("userIdx") Long userIdx);

}
