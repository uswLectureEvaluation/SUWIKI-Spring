package usw.suwiki.global.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.user.user.service.UserBusinessService;
import usw.suwiki.global.annotation.ApiLogger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/v2/token")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class JwtController {

    private final UserBusinessService userBusinessService;

    // Web 토큰 갱신
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/client-refresh")
    public Map<String, String> clientTokenRefresh(
            @CookieValue(value = "refreshToken") Cookie requestRefreshCookie,
            HttpServletResponse response
    ) {
        Map<String, String> tokenPair = userBusinessService.executeJWTRefreshForWebClient(
                requestRefreshCookie
        );
        Cookie refreshCookie = new Cookie("refreshToken", tokenPair.get("RefreshToken"));
        refreshCookie.setMaxAge(14 * 24 * 60 * 60);
        refreshCookie.setSecure(true);
        refreshCookie.setHttpOnly(true);
        response.addCookie(refreshCookie);
        return new HashMap<>() {{
            put("AccessToken", tokenPair.get("AccessToken"));
        }};
    }

    // Mobile 토큰 갱신
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/mobile-refresh")
    public Map<String, String> tokenRefresh(
            @Valid @RequestHeader String Authorization
    ) {
        return userBusinessService.executeJWTRefreshForMobileClient(Authorization);
    }
}
