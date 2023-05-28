package usw.suwiki.domain.refreshToken.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.user.user.service.UserBusinessService;
import usw.suwiki.global.ResponseForm;
import usw.suwiki.global.annotation.ApiLogger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/v2/refreshtoken")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RefreshTokenControllerV2 {

    private final UserBusinessService userBusinessService;

     @ApiOperation(
             value = "Web Client 토큰 갱신",
             notes = "토큰을 갱신한다."
     )
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/web-client/refresh")
    public ResponseForm clientTokenRefresh(
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
        return ResponseForm.success(new HashMap<>() {{
            put("AccessToken", tokenPair.get("AccessToken"));
        }});
    }

     @ApiOperation(
             value = "Mobile Client 토큰 갱신",
             notes = "토큰을 갱신한다."
     )
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/mobile-client/refresh")
    public ResponseForm tokenRefresh(
            @Valid @RequestHeader String Authorization
    ) {
        return ResponseForm.success(userBusinessService.executeJWTRefreshForMobileClient(Authorization));
    }
}
