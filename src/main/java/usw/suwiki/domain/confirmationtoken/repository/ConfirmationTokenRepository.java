package usw.suwiki.domain.confirmationtoken.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usw.suwiki.domain.confirmationtoken.entity.ConfirmationToken;

import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long>, ConfirmationTokenQueryDslRepository {

    Optional<ConfirmationToken> findByToken(String token);

    Optional<ConfirmationToken> findByUserIdx(Long userIdx);

    void deleteByUserIdx(Long userIdx);

}
