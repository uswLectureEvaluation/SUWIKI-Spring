package usw.suwiki.domain.user.service.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.dto.UserRequestDto.LoginForm;
import usw.suwiki.domain.user.entity.User;
import usw.suwiki.domain.user.service.UserService;
import usw.suwiki.domain.userIsolation.repository.UserIsolationRepository;
import usw.suwiki.domain.userIsolation.service.UserIsolationService;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtTokenProvider;
import usw.suwiki.global.jwt.JwtTokenResolver;

import java.util.HashMap;
import java.util.Map;

import static usw.suwiki.global.exception.ErrorType.PASSWORD_ERROR;

@Service
@RequiredArgsConstructor
@Transactional
public class UserLoginUseCase {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenResolver jwtTokenResolver;
    private final UserIsolationRepository userIsolationRepository;
    private final UserService userService;
    private final UserIsolationService userIsolationService;

    public Map<String, String> execute(LoginForm loginForm) {
        Map<String, String> tokenPair = new HashMap<>();
        if (userIsolationRepository.findByLoginId(loginForm.getLoginId()).isEmpty()) {
            User notSleepingUser = userService.loadUserFromLoginId(loginForm.getLoginId());
            userService.isUserEmailAuth(notSleepingUser.getId());
            if (userService.validatePasswordAtUserTable(loginForm.getLoginId(), loginForm.getPassword())) {
                String accessToken = jwtTokenProvider.createAccessToken(notSleepingUser);
                String refreshToken = jwtTokenResolver.refreshTokenUpdateOrCreate(notSleepingUser);
                tokenPair.put("AccessToken", accessToken);
                tokenPair.put("RefreshToken", refreshToken);
                userService.setLastLogin(notSleepingUser);
                return tokenPair;
            }
            throw new AccountException(PASSWORD_ERROR);
        }
        else if (userIsolationRepository.findByLoginId(loginForm.getLoginId()).isPresent()) {
            User sleepingUser = userIsolationService.sleepingUserLogin(loginForm);
            String accessToken = jwtTokenProvider.createAccessToken(sleepingUser);
            String refreshToken = jwtTokenResolver.refreshTokenUpdateOrCreate(sleepingUser);
            tokenPair.put("AccessToken", accessToken);
            tokenPair.put("RefreshToken", refreshToken);
            userService.setLastLogin(sleepingUser);
            return tokenPair;
        }
        throw new AccountException(PASSWORD_ERROR);
    }
}
