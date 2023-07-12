package usw.suwiki.domain.confirmationtoken.repository;

import usw.suwiki.domain.confirmationtoken.ConfirmationToken;

import java.time.LocalDateTime;
import java.util.List;

public interface ConfirmationTokenQueryDslRepository {
    List<ConfirmationToken> loadNotConfirmedTokensByExpiresAtIsNull(LocalDateTime localDateTime);
}
