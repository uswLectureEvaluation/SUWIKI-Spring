package usw.suwiki.domain.confirmationtoken.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.confirmationtoken.entity.ConfirmationToken;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.domain.user.user.service.UserService;
import usw.suwiki.global.util.emailBuild.BuildEmailAuthFailedForm;
import usw.suwiki.global.util.emailBuild.BuildEmailAuthSuccessForm;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;
    private final UserRepository userRepository;
    private final BuildEmailAuthFailedForm buildEmailAuthFailedForm;
    private final BuildEmailAuthSuccessForm buildEmailAuthSuccessForm;


    //이메일 인증 토큰 검증
    @Transactional
    public String confirmToken(String token) {
        Optional<ConfirmationToken> confirmationToken = confirmationTokenService.getToken(token);
        if (confirmationToken.isPresent()) {
            if (confirmationToken.get().isVerified()) {
                confirmationTokenService.deleteAllByToken(token);
                userRepository.deleteById(confirmationToken.get().getUserIdx());
                return buildEmailAuthFailedForm.tokenIsExpired();
            }
            confirmationToken.get().updateConfirmedAt();
            userService.loadUserFromUserIdx(confirmationToken.get().getUserIdx())
                .activateUser();
            return buildEmailAuthSuccessForm.buildEmail();
        }
        return buildEmailAuthFailedForm.internalError();
    }
}