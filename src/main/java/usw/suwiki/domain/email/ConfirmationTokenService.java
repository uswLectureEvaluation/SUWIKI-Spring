package usw.suwiki.domain.email;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.UserRepository;
import usw.suwiki.domain.userIsolation.UserIsolationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

    private final UserRepository userRepository;
    private final UserIsolationRepository userIsolationRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    //토큰 정보 저장
    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    //토큰 파싱
    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    //이메일 인증 토큰 인증시각 스탬프
    public void setConfirmedAt(String token) {
        confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }

    //이메일 인증 토큰 삭제(토큰 ID로 삭제)
    public void deleteAllById(Long tokenId) {
        confirmationTokenRepository.deleteById(tokenId);
    }

    //이메일 인증 토큰 삭제 (토큰 값으로 삭제)
    public void deleteAllByToken(String token) {
        confirmationTokenRepository.deleteAllByTokenInQuery(token);
    }


    // 이메일 인증 안한 유저는 매 분마다 검사하여 삭제
    @Transactional
    @Scheduled(cron = "0 * * * * * ")
    public void isNotConfirmedEmail() {
        List<ConfirmationToken> targetUser = confirmationTokenRepository.isUserConfirmed(LocalDateTime.now());

        for (ConfirmationToken confirmationToken : targetUser) {
            Long targetUserIdx = confirmationToken.getUserIdx();

            confirmationTokenRepository.deleteById(confirmationToken.getId());

            userRepository.deleteById(targetUserIdx);

            userIsolationRepository.deleteByUserIdx(targetUserIdx);
        }
    }
}
