package usw.suwiki.domain.refreshtoken.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.refreshtoken.RefreshToken;
import usw.suwiki.domain.refreshtoken.repository.RefreshTokenRepository;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.core.exception.errortype.AccountException;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenCRUDService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void save(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    public void deleteFromUserIdx(Long userIdx) {
        refreshTokenRepository.deleteByUserIdx(userIdx);
    }

    @Transactional(readOnly = true)
    public Optional<RefreshToken> loadRefreshTokenFromUserIdx(Long userIdx) {
        return refreshTokenRepository.findByUserIdx(userIdx);
    }

    @Transactional(readOnly = true)
    public RefreshToken loadRefreshTokenFromPayload(String payload) {
        return refreshTokenRepository.findByPayload(payload)
            .orElseThrow(() -> new AccountException(ExceptionType.TOKEN_IS_BROKEN));
    }
}
