package usw.suwiki.auth.token.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.auth.token.ConfirmationToken;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ConfirmationTokenBusinessService {
    private final ConfirmUserService confirmUserService;
    private final ConfirmationTokenCRUDService confirmationTokenCRUDService;

    public String confirmToken(String token) {
        Optional<ConfirmationToken> wrappedConfirmationToken =
            confirmationTokenCRUDService.loadConfirmationTokenFromPayload(token);

        if (wrappedConfirmationToken.isPresent()) {
            ConfirmationToken confirmationToken = wrappedConfirmationToken.get();
            if (confirmationToken.isTokenExpired()) {
                confirmationTokenCRUDService.deleteFromId(confirmationToken.getId());
                confirmUserService.delete(confirmationToken.getUserIdx());
                return buildEmailAuthFailedForm.tokenIsExpired();
            }
            confirmationToken.updateConfirmedAt();
            confirmUserService.activated(confirmationToken.getUserIdx());
            return buildEmailAuthSuccessForm.buildEmail();
        }
        return buildEmailAuthFailedForm.internalError();
    }
}
