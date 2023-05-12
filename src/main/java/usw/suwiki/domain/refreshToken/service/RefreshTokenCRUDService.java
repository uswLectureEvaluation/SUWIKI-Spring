package usw.suwiki.domain.refreshToken.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.refreshToken.RefreshToken;
import usw.suwiki.domain.refreshToken.repository.RefreshTokenRepository;

import java.util.Optional;

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
    public Optional<RefreshToken> loadRefreshTokenFromPayload(String payload) {
        return refreshTokenRepository.findByPayload(payload);
    }
}
