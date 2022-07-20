package usw.suwiki.global.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenValidator {

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    //AccessToken 만료날짜 확인
    public boolean validateAccessToken(String accessToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(accessToken);
            return true;
        } catch (SignatureException | MalformedJwtException | IllegalArgumentException ex) {
            throw new BadCredentialsException("INVALID", ex);
        } catch (ExpiredJwtException exception) {
            throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
        }
    }

    // 2022/03/26 오늘날짜
    // 2022/03/25 만료날짜
    // 만료되면 401 Error 내려보내기 , 실제 이 서비스를 가져다 쓸때에는 true 로 전달해서 조건문이 발동하도록 해야한다.

    //RefreshToken 만료날짜 확인
    public boolean validateRefreshToken(String refreshToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(refreshToken);
            return true;
        } catch (SignatureException | MalformedJwtException | IllegalArgumentException ex) {
            throw new BadCredentialsException("INVALID", ex);
        } catch (ExpiredJwtException exception) {
            throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
        }
    }

    // 리프레시토큰 유효기간이 현재시간 - 7일보다 더 이전의 기간인지 판별
    // True 면 업데이트 해줘야함
    public boolean isNeedToUpdateRefreshToken(String refreshToken) {
        Date claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(refreshToken).getBody().getExpiration();
        
        // Jwt Claims LocalDateTime 으로 형변환
        LocalDateTime localDateTimeClaims = claims.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        // 현재시간 - 7일(초단위) 를 한 피연산자 할당
        LocalDateTime subDetractedDateTime = LocalDateTime.now().plusSeconds(604800);

        // 피연산자 보다 이전 이면 True 반환 및 갱신해줘야함
        return localDateTimeClaims.isBefore(subDetractedDateTime);
    }
}
