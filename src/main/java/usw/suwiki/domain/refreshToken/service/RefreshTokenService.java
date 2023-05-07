package usw.suwiki.domain.refreshToken.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.refreshToken.entity.RefreshToken;
import usw.suwiki.domain.refreshToken.repository.RefreshTokenRepository;
import usw.suwiki.global.exception.errortype.AccountException;

import java.util.Optional;

import static usw.suwiki.global.exception.ExceptionType.LOGIN_REQUIRED;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void deleteFromUserIdx(Long userIdx) {
        refreshTokenRepository.deleteByUserIdx(userIdx);
    }

    public RefreshToken loadRefreshTokenFromPayload(String payload) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByPayload(payload);
        if (refreshToken.isEmpty()) {
            throw new AccountException(LOGIN_REQUIRED);
        }
        return refreshToken.get();
    }
}
