package usw.suwiki.auth.token;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfirmationTokenRepository
  extends JpaRepository<ConfirmationToken, Long>, ConfirmationTokenQueryDslRepository {

    Optional<ConfirmationToken> findByToken(String token);

    Optional<ConfirmationToken> findByUserIdx(Long userIdx);

    void deleteByUserIdx(Long userIdx);

}
