package usw.suwiki.domain.email.service;

import static usw.suwiki.global.exception.ErrorType.EMAIL_AUTH_TOKEN_ALREADY_USED;
import static usw.suwiki.global.exception.ErrorType.EMAIL_VALIDATED_ERROR;
import static usw.suwiki.global.exception.ErrorType.EMAIL_VALIDATED_ERROR_RETRY;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.email.entity.ConfirmationToken;
import usw.suwiki.domain.user.repository.UserRepository;
import usw.suwiki.domain.user.service.UserCommonService;
import usw.suwiki.global.exception.errortype.AccountException;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private final UserCommonService userCommonService;
    private final ConfirmationTokenService confirmationTokenService;
    private final UserRepository userRepository;

    //이메일 인증 토큰 검증
    @Transactional
    public void confirmToken(String token) {
        Optional<ConfirmationToken> confirmationToken = confirmationTokenService.getToken(token);
        if (confirmationToken.isPresent()) {
            if (confirmationToken.get().getConfirmedAt() != null) {
                throw new AccountException(EMAIL_AUTH_TOKEN_ALREADY_USED);
            } else if (userCommonService.isEmailAuthTokenExpired(confirmationToken.get())) {
                confirmationTokenService.deleteAllByToken(token);
                userRepository.deleteById(confirmationToken.get().getUserIdx());
                throw new AccountException(EMAIL_VALIDATED_ERROR_RETRY);
            }
            else {
                confirmationTokenService.setConfirmedAt(token);
                return;
            }
        }
        throw new AccountException(EMAIL_VALIDATED_ERROR);
    }

    @Transactional
    public void mailAuthSuccess(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
            .orElseThrow(() -> new AccountException(EMAIL_VALIDATED_ERROR));

        Long userIdx = confirmationToken.getUserIdx();
        userRepository.updateUserEmailAuthStatus(userIdx);
    }
}