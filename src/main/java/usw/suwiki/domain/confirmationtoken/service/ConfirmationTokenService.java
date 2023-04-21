package usw.suwiki.domain.confirmationtoken.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.confirmationtoken.entity.ConfirmationToken;
import usw.suwiki.domain.confirmationtoken.repository.ConfirmationTokenRepository;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.domain.user.userIsolation.repository.UserIsolationRepository;
import usw.suwiki.global.util.emailBuild.BuildEmailAuthFailedForm;
import usw.suwiki.global.util.emailBuild.BuildEmailAuthSuccessForm;

@Service
@AllArgsConstructor
@Transactional
public class ConfirmationTokenService {

    private final UserRepository userRepository;
    private final UserIsolationRepository userIsolationRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final BuildEmailAuthFailedForm buildEmailAuthFailedForm;
    private final BuildEmailAuthSuccessForm buildEmailAuthSuccessForm;

    public String confirmToken(String token) {
        Optional<ConfirmationToken> confirmationToken = getToken(token);
        if (confirmationToken.isPresent()) {
            if (confirmationToken.get().isVerified()) {
                deleteAllByToken(token);
                userRepository.deleteById(confirmationToken.get().getUserIdx());
                return buildEmailAuthFailedForm.tokenIsExpired();
            }
            confirmationToken.get().updateConfirmedAt();
            userRepository.findById(confirmationToken.get().getUserIdx()).get().activateUser();
            return buildEmailAuthSuccessForm.buildEmail();
        }
        return buildEmailAuthFailedForm.internalError();
    }

    // 토큰 정보 저장
    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    @Transactional(readOnly = true)
    // 토큰 파싱
    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    // 이메일 인증 토큰 삭제 (토큰 값으로 삭제)
    public void deleteAllByToken(String token) {
        confirmationTokenRepository.deleteAllByTokenInQuery(token);
    }


    // 이메일 인증 안한 유저는 매 분마다 검사하여 삭제
    @Transactional
    @Scheduled(cron = "0 0 * * * * ")
    public void isNotConfirmedEmail() {
        List<ConfirmationToken> targetUser =
            confirmationTokenRepository.isUserConfirmed(LocalDateTime.now());
        for (ConfirmationToken confirmationToken : targetUser) {
            Long targetUserIdx = confirmationToken.getUserIdx();
            confirmationTokenRepository.deleteById(confirmationToken.getId());
            userRepository.deleteById(targetUserIdx);
            userIsolationRepository.deleteByUserIdx(targetUserIdx);
        }
    }
}
