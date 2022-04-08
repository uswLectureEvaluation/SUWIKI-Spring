package usw.suwiki.service.emailToken;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import usw.suwiki.domain.emailToken.ConfirmationToken;
import usw.suwiki.repository.emailToken.ConfirmationTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

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
        confirmationTokenRepository.deleteAllByIdInQuery(tokenId);
    }

    //이메일 인증 토큰 삭제 (토큰 값으로 삭제)
    public void deleteAllByToken(String token) {
        confirmationTokenRepository.deleteAllByTokenInQuery(token);
    }
}
