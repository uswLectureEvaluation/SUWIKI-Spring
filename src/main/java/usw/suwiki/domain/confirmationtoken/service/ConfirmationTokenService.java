package usw.suwiki.domain.confirmationtoken.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.confirmationtoken.entity.ConfirmationToken;
import usw.suwiki.domain.confirmationtoken.repository.ConfirmationTokenRepository;
import usw.suwiki.domain.user.user.entity.User;
import usw.suwiki.domain.user.user.service.UserService;
import usw.suwiki.global.util.emailBuild.BuildEmailAuthFailedForm;
import usw.suwiki.global.util.emailBuild.BuildEmailAuthSuccessForm;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class ConfirmationTokenService {

    private final UserService userService;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final BuildEmailAuthFailedForm buildEmailAuthFailedForm;
    private final BuildEmailAuthSuccessForm buildEmailAuthSuccessForm;

    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    @Transactional(readOnly = true)
    public Optional<ConfirmationToken> loadConfirmationTokenFromPayload(String payload) {
        return confirmationTokenRepository.findByToken(payload);
    }

    public String confirmToken(String token) {
        Optional<ConfirmationToken> wrappedConfirmationToken = loadConfirmationTokenFromPayload(token);
        if (wrappedConfirmationToken.isPresent()) {
            ConfirmationToken confirmationToken = wrappedConfirmationToken.get();
            if (confirmationToken.isTokenExpired()) {
                confirmationTokenRepository.deleteById(confirmationToken.getId());
                userService.deleteFromUserIdx(confirmationToken.getUserIdx());
                return buildEmailAuthFailedForm.tokenIsExpired();
            }
            confirmationToken.updateConfirmedAt();
            userService.loadUserFromUserIdx(confirmationToken.getUserIdx()).activateUser();
            return buildEmailAuthSuccessForm.buildEmail();
        }
        return buildEmailAuthFailedForm.internalError();
    }

    // 이메일 인증 안한 유저는 매 분마다 검사하여 삭제
    @Transactional
    @Scheduled(cron = "0 0 * * * * ")
    public void isNotConfirmedEmail() {
        List<ConfirmationToken> targetTokens =
                confirmationTokenRepository.loadNotConfirmedTokensByExpiredAt(LocalDateTime.now());
        for (ConfirmationToken confirmationToken : targetTokens) {
            User targetUser = userService.loadUserFromUserIdx(confirmationToken.getUserIdx());
            confirmationTokenRepository.deleteById(confirmationToken.getId());
            userService.deleteFromUserIdx(targetUser.getId());
        }
    }

    public void deleteFromUserIdx(Long userIdx) {
        confirmationTokenRepository.deleteByUserIdx(userIdx);
    }
}
