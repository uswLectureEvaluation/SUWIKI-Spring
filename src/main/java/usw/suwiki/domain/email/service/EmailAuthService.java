package usw.suwiki.domain.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.email.entity.ConfirmationToken;
import usw.suwiki.domain.user.entity.Role;
import usw.suwiki.domain.user.repository.UserRepository;
import usw.suwiki.domain.user.service.UserService;
import usw.suwiki.global.exception.ErrorType;
import usw.suwiki.global.exception.errortype.AccountException;

import java.time.LocalDateTime;

import static usw.suwiki.global.exception.ErrorType.EMAIL_AUTH_TOKEN_ALREADY_USED;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;
    private final UserRepository userRepository;

    //이메일 인증 토큰 검증
    @Transactional
    public void confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
                .orElseThrow(() -> new AccountException(ErrorType.EMAIL_VALIDATED_ERROR));
        if (confirmationToken.getConfirmedAt() != null)
            throw new AccountException(EMAIL_AUTH_TOKEN_ALREADY_USED);
        else if (userService.isEmailAuthTokenExpired(confirmationToken)) {
            userRepository.deleteById(confirmationToken.getUserIdx());
            confirmationTokenService.deleteAllByToken(token);
        }
        confirmationTokenService.setConfirmedAt(token);
    }

    @Transactional
    public void mailAuthSuccess(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
                .orElseThrow(() -> new AccountException(ErrorType.EMAIL_VALIDATED_ERROR));

        Long userIdx = confirmationToken.getUserIdx();
        userService.loadUserFromUserIdx(userIdx)
                .setRestricted(false);
        userService.loadUserFromUserIdx(userIdx)
                .setCreatedAt(LocalDateTime.now());
        userService.loadUserFromUserIdx(userIdx)
                .setUpdatedAt(LocalDateTime.now());
        userService.loadUserFromUserIdx(userIdx)
                .setRole(Role.USER);
    }
}