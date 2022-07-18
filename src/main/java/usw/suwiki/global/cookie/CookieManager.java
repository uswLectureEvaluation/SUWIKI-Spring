package usw.suwiki.global.cookie;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.global.jwt.JwtTokenProvider;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.jwt.JwtTokenValidator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class CookieManager {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenResolver jwtTokenResolver;
    private final JwtTokenValidator jwtTokenValidator;


    // 쿠키 조회
    public String getCookie(HttpServletRequest req){

        // 모든 쿠키 가져오기
        Cookie[] cookies = req.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // 쿠키 이름 가져오기
                String cookieName = cookie.getName();

                // 쿠키 값 가져오기
                String refreshTokenValueOfCookie = cookie.getValue();

                if (cookieName.equals("RefreshToken")) {
                    jwtTokenValidator.validateRefreshToken(refreshTokenValueOfCookie);

                    return refreshTokenValueOfCookie;
                }
            }
        }
        throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
    }
}
