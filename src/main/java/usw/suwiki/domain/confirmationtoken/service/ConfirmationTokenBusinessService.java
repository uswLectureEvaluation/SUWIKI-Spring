package usw.suwiki.domain.confirmationtoken.service;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.confirmationtoken.ConfirmationToken;
import usw.suwiki.domain.user.user.service.UserCRUDService;
import usw.suwiki.global.util.emailBuild.BuildEmailAuthFailedForm;
import usw.suwiki.global.util.emailBuild.BuildEmailAuthSuccessForm;

@Service
@AllArgsConstructor
@Transactional
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
