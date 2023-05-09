package usw.suwiki.domain.refreshToken.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.refreshToken.RefreshToken;
import usw.suwiki.domain.refreshToken.repository.RefreshTokenRepository;
import usw.suwiki.global.exception.errortype.AccountException;

import java.util.Optional;

import static usw.suwiki.global.exception.ExceptionType.LOGIN_REQUIRED;
import static usw.suwiki.global.exception.ExceptionType.USER_NOT_EXISTS;

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
    public RefreshToken loadRefreshTokenFromUserIdx(Long userIdx) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUserIdx(userIdx);
        if (refreshToken.isEmpty()) {
            throw new AccountException(USER_NOT_EXISTS);
        }
        return refreshToken.get();
    }

    @Transactional(readOnly = true)
    public RefreshToken loadRefreshTokenFromPayload(String payload) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByPayload(payload);
        if (refreshToken.isEmpty()) {
            throw new AccountException(LOGIN_REQUIRED);
        }
        return refreshToken.get();
    }
}
