package usw.suwiki.domain.user.service.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.refreshToken.repository.RefreshTokenRepository;
import usw.suwiki.domain.user.entity.User;
import usw.suwiki.domain.user.service.UserService;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtTokenProvider;
import usw.suwiki.global.jwt.JwtTokenResolver;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;

import static usw.suwiki.global.exception.ErrorType.USER_RESTRICTED;

@Service
@RequiredArgsConstructor
@Transactional
public class UserTokenRefreshUseCase {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenResolver jwtTokenResolver;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    public Map<String, String> executeForWebClient(Cookie requestRefreshCookie) {
        String refreshToken = requestRefreshCookie.getValue();
        if (refreshTokenRepository.findByPayload(refreshToken).isEmpty())
            throw new AccountException(USER_RESTRICTED);
        Long userIdx = refreshTokenRepository.findByPayload(refreshToken).get().getUserIdx();
        User user = userService.loadUserFromUserIdx(userIdx);
        String accessToken = jwtTokenProvider.createAccessToken(user);
        String newRefreshToken = jwtTokenResolver.refreshTokenUpdateOrCreate(user);
        Map<String, String> tokenPair = new HashMap<>();
        tokenPair.put("AccessToken", accessToken);
        tokenPair.put("RefreshToken", newRefreshToken);
        userService.setLastLogin(user);
        return tokenPair;
    }

    public Map<String, String> executeForMobileClient(String Authorization) {
        if (refreshTokenRepository.findByPayload(Authorization).isEmpty())
            throw new AccountException(USER_RESTRICTED);
        Long userIdx = refreshTokenRepository.findByPayload(Authorization).get().getUserIdx();
        User user = userService.loadUserFromUserIdx(userIdx);
        String newRefreshToken = jwtTokenResolver.refreshTokenUpdateOrCreate(user);
        Map<String, String> tokenPair = new HashMap<>();
        tokenPair.put("AccessToken", jwtTokenProvider.createAccessToken(user));
        tokenPair.put("RefreshToken", newRefreshToken);
        userService.setLastLogin(user);
        return tokenPair;
    }
}
