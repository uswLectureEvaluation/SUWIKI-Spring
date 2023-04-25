package usw.suwiki.global.jwt;

import static usw.suwiki.global.exception.ExceptionType.TOKEN_IS_NOT_FOUND;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import usw.suwiki.global.exception.errortype.AccountException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtValidator {

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    public void validateJwt(String accessToken) {
        try {
            Jwts
                .parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(accessToken);
        } catch (SignatureException |
                 MalformedJwtException |
                 IllegalArgumentException ex
        ) {
            throw new BadCredentialsException("잘못된 토큰 정보입니다.", ex);
        } catch (ExpiredJwtException exception) {
            throw new AccountException(TOKEN_IS_NOT_FOUND);
        }
    }
}
