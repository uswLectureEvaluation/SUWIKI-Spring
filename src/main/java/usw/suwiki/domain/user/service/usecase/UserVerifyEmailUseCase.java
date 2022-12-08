package usw.suwiki.domain.user.service.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.email.service.EmailAuthService;
import usw.suwiki.global.util.emailBuild.BuildEmailAuthSuccessForm;

@Service
@RequiredArgsConstructor
@Transactional
public class UserVerifyEmailUseCase {

    private final BuildEmailAuthSuccessForm buildEmailAuthSuccessForm;
    private final EmailAuthService emailAuthService;

    public String execute(String token) {
        String result = buildEmailAuthSuccessForm.buildEmail();
        emailAuthService.confirmToken(token);
        emailAuthService.mailAuthSuccess(token);
        return result;
    }
}
