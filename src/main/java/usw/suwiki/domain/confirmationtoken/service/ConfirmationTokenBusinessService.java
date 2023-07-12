package usw.suwiki.domain.confirmationtoken.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.confirmationtoken.ConfirmationToken;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.service.UserCRUDService;
import usw.suwiki.global.util.emailBuild.BuildEmailAuthFailedForm;
import usw.suwiki.global.util.emailBuild.BuildEmailAuthSuccessForm;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    // 이메일 인증 안한 유저는 매 분마다 검사하여 삭제
    @Transactional
    @Scheduled(cron = "12 * * * * * ")
    public void isNotConfirmedEmail() {
        List<ConfirmationToken> confirmationTokens = confirmationTokenCRUDService.loadNotConfirmedTokens(
                LocalDateTime.now().minusMinutes(30)
        );
        for (ConfirmationToken confirmationToken : confirmationTokens) {
            User targetUser = userCRUDService.loadUserFromUserIdx(confirmationToken.getUserIdx());
            confirmationTokenCRUDService.deleteFromId(confirmationToken.getId());
            userCRUDService.deleteFromUserIdx(targetUser.getId());
        }
    }
}
