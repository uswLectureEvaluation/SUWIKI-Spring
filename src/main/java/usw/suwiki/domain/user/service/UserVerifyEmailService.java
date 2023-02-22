package usw.suwiki.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.email.service.EmailAuthService;

@Service
@RequiredArgsConstructor
@Transactional
public class UserVerifyEmailService {

    private final EmailAuthService emailAuthService;

    public String execute(String token) {
        return emailAuthService.confirmToken(token);
    }
}
