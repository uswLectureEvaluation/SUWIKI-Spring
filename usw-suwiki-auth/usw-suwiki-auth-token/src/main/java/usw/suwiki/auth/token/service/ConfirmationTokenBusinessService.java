package usw.suwiki.auth.token.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.auth.token.ConfirmationToken;
import usw.suwiki.domain.user.service.UserCRUDService;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ConfirmationTokenBusinessService {
    private final UserCRUDService userCRUDService;
    private final ConfirmationTokenCRUDService confirmationTokenCRUDService;

    private final BuildEmailAuthFailedForm buildEmailAuthFailedForm;
    private final BuildEmailAuthSuccessForm buildEmailAuthSuccessForm;

    public String confirmToken(String token) {
        Optional<ConfirmationToken> wrappedConfirmationToken =
            confirmationTokenCRUDService.loadConfirmationTokenFromPayload(token);

        if (wrappedConfirmationToken.isPresent()) {
            ConfirmationToken confirmationToken = wrappedConfirmationToken.get();
            if (confirmationToken.isTokenExpired()) {
                confirmationTokenCRUDService.deleteFromId(confirmationToken.getId());
                userCRUDService.deleteFromUserIdx(confirmationToken.getUserIdx());
                return buildEmailAuthFailedForm.tokenIsExpired();
            }
            confirmationToken.updateConfirmedAt();
            userCRUDService.loadUserFromUserIdx(confirmationToken.getUserIdx()).activateUser();
            return buildEmailAuthSuccessForm.buildEmail();
        }
        return buildEmailAuthFailedForm.internalError();
    }

}
