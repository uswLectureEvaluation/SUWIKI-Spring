package usw.suwiki.auth.token.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.auth.token.ConfirmationToken;
import usw.suwiki.auth.token.ConfirmationTokenRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConfirmationTokenCRUDService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Transactional
    public void saveConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationTokenRepository.save(confirmationToken);
    }

    public Optional<ConfirmationToken> loadConfirmationTokenFromUserIdx(Long userIdx) {
        return confirmationTokenRepository.findByUserIdx(userIdx);
    }

    public Optional<ConfirmationToken> loadConfirmationTokenFromPayload(String payload) {
        return confirmationTokenRepository.findByToken(payload);
    }

    public List<ConfirmationToken> loadNotConfirmedTokens(LocalDateTime targetTime) {
        return confirmationTokenRepository.loadNotConfirmedTokensByExpiresAtIsNull(targetTime);
    }

    @Transactional
    public void deleteFromId(Long id) {
        confirmationTokenRepository.deleteById(id);
    }

    @Transactional
    public void deleteFromUserIdx(Long userIdx) {
        confirmationTokenRepository.deleteByUserIdx(userIdx);
    }
}
