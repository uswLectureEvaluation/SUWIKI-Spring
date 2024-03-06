package usw.suwiki.auth.token;

import java.time.LocalDateTime;
import java.util.List;

public interface ConfirmationTokenQueryDslRepository {
    List<ConfirmationToken> loadNotConfirmedTokensByExpiresAtIsNull(LocalDateTime localDateTime);
}
