package usw.suwiki.auth.token.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.auth.token.RefreshToken;
import usw.suwiki.auth.token.RefreshTokenRepository;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RefreshTokenCRUDService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void save(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void deleteFromUserIdx(Long userIdx) {
        refreshTokenRepository.deleteByUserIdx(userIdx);
    }

    public Optional<RefreshToken> loadRefreshTokenFromUserIdx(Long userIdx) {
        return refreshTokenRepository.findByUserIdx(userIdx);
    }

    public RefreshToken loadRefreshTokenFromPayload(String payload) {
        return refreshTokenRepository.findByPayload(payload)
            .orElseThrow(() -> new AccountException(ExceptionType.TOKEN_IS_BROKEN));
    }
}
