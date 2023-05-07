package usw.suwiki.domain.confirmationtoken.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.confirmationtoken.entity.ConfirmationToken;
import usw.suwiki.domain.confirmationtoken.repository.ConfirmationTokenRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ConfirmationTokenCRUDService {

    private final ConfirmationTokenService confirmationTokenService;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    public void callSaveConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationTokenService.saveConfirmationToken(confirmationToken);
    }

    public Optional<ConfirmationToken> findByUserIdx(Long userIdx) {
        return confirmationTokenRepository.findByUserIdx(userIdx);
    }

}