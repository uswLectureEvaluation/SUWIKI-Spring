package usw.suwiki.domain.email.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.email.entity.ConfirmationToken;
import usw.suwiki.domain.user.repository.UserRepository;
import usw.suwiki.domain.user.service.UserCommonService;
import usw.suwiki.global.util.emailBuild.BuildEmailAuthFailedForm;
import usw.suwiki.global.util.emailBuild.BuildEmailAuthSuccessForm;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private final UserCommonService userCommonService;
    private final ConfirmationTokenService confirmationTokenService;
    private final UserRepository userRepository;
    private final BuildEmailAuthFailedForm buildEmailAuthFailedForm;
    private final BuildEmailAuthSuccessForm buildEmailAuthSuccessForm;


    //이메일 인증 토큰 검증
    @Transactional
    public String confirmToken(String token) {
        Optional<ConfirmationToken> confirmationToken = confirmationTokenService.getToken(token);
        if (confirmationToken.isPresent()) {
            if (confirmationToken.get().getConfirmedAt() != null) {
                return buildEmailAuthFailedForm.tokenIsAlreadyUsed();
                // throw new AccountException(EMAIL_AUTH_TOKEN_ALREADY_USED);
            } else if (userCommonService.isEmailAuthTokenExpired(confirmationToken.get())) {
                confirmationTokenService.deleteAllByToken(token);
                userRepository.deleteById(confirmationToken.get().getUserIdx());
                return buildEmailAuthFailedForm.tokenIsExpired();
                // throw new AccountException(EMAIL_VALIDATED_ERROR_RETRY);
            } else {
                confirmationTokenService.setConfirmedAt(token);
                Long userIdx = confirmationToken.get().getUserIdx();
                userRepository.updateUserEmailAuthStatus(userIdx);
                return buildEmailAuthSuccessForm.buildEmail();
            }
        }
        return buildEmailAuthFailedForm.internalError();
        // throw new AccountException(EMAIL_VALIDATED_ERROR);
    }
}