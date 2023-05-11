package usw.suwiki.domain.confirmationtoken.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.confirmationtoken.ConfirmationToken;
import usw.suwiki.domain.confirmationtoken.repository.ConfirmationTokenRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class ConfirmationTokenCRUDService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    public void saveConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationTokenRepository.save(confirmationToken);
    }

    @Transactional(readOnly = true)
    public Optional<ConfirmationToken> loadConfirmationTokenFromUserIdx(Long userIdx) {
        return confirmationTokenRepository.findByUserIdx(userIdx);
    }

    @Transactional(readOnly = true)
    public Optional<ConfirmationToken> loadConfirmationTokenFromPayload(String payload) {
        return confirmationTokenRepository.findByToken(payload);
    }

    public void deleteFromId(Long id) {
        confirmationTokenRepository.deleteById(id);
    }

    public void deleteFromUserIdx(Long userIdx) {
        confirmationTokenRepository.deleteByUserIdx(userIdx);
    }

    public List<ConfirmationToken> loadNotConfirmedTokensByExpiredAt(LocalDateTime time) {
        return confirmationTokenRepository.loadNotConfirmedTokensByExpiredAt(time);
    }
}