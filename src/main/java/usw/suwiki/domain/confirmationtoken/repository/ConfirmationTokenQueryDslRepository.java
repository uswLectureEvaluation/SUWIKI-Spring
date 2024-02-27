package usw.suwiki.domain.confirmationtoken.repository;

import java.time.LocalDateTime;
import java.util.List;
import usw.suwiki.domain.confirmationtoken.ConfirmationToken;

public interface ConfirmationTokenQueryDslRepository {

    List<ConfirmationToken> loadNotConfirmedTokensByExpiresAtIsNull(LocalDateTime localDateTime);
}
