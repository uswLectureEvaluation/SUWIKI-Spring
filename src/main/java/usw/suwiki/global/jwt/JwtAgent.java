package usw.suwiki.global.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.refreshToken.RefreshToken;
import usw.suwiki.domain.refreshToken.service.RefreshTokenCRUDService;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.global.exception.errortype.AccountException;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

import static io.jsonwebtoken.Jwts.parser;
import static usw.suwiki.global.exception.ExceptionType.TOKEN_IS_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class JwtAgent {

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    private static final Long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L; // 30분
    private static final Long REFRESH_TOKEN_EXPIRE_TIME = 270 * 24 * 60 * 60 * 1000L; // 270일 -> 9개월
//    private static final Long ACCESS_TOKEN_EXPIRE_TIME = 1 * 1000L; // 1초
//    private static final Long REFRESH_TOKEN_EXPIRE_TIME = 5 * 60 * 1000L; // 5분

    private final RefreshTokenCRUDService refreshTokenCRUDService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

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

    public String createAccessToken(User user) {
        return buildAccessToken(
                setAccessTokenClaimsByUser(user),
                new Date(new Date().getTime() + ACCESS_TOKEN_EXPIRE_TIME)
        );
    }

    public String createRefreshToken(User user) {
        String buildRefreshToken = buildRefreshToken(new Date(new Date().getTime() + REFRESH_TOKEN_EXPIRE_TIME));
        refreshTokenCRUDService.save(RefreshToken.buildRefreshToken(user.getId(), buildRefreshToken));

        return buildRefreshToken;
    }

    @Transactional
    public String reIssueRefreshToken(RefreshToken refreshToken) {
        refreshToken.updatePayload(
                buildRefreshToken(new Date(new Date().getTime() + REFRESH_TOKEN_EXPIRE_TIME))
        );
        return refreshToken.getPayload();
    }

    private Claims setAccessTokenClaimsByUser(User user) {
        Claims claims = Jwts.claims();
        claims.setSubject(user.getLoginId());
        claims.put("id", user.getId());
        claims.put("loginId", user.getLoginId());
        claims.put("role", user.getRole());
        claims.put("restricted", user.getRestricted());
        return claims;
    }

    private String buildAccessToken(Claims claims, Date accessTokenExpireIn) {
        return Jwts.builder()
                .setHeaderParam("type", "JWT")
                .setClaims(claims)
                .setExpiration(accessTokenExpireIn)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    private String buildRefreshToken(Date refreshTokenExpireIn) {
        return Jwts.builder()
                .setHeaderParam("type", "JWT")
                .setExpiration(refreshTokenExpireIn)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public Long getId(String token) {
        validateJwt(token);
        Object id = parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token)
                .getBody().get("id");
        return Long.valueOf(String.valueOf(id));
    }

    public String getUserRole(String token) {
        validateJwt(token);
        return (String) parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token).getBody()
                .get("role");
    }

    public Boolean getUserIsRestricted(String token) {
        validateJwt(token);
        return (Boolean) parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token)
                .getBody().get("restricted");
    }

    /**
     * 로그인 요청 시 리프레시 토큰을 갱신해 줘야 할지 판단한다.
     */
    @Transactional
    public String judgementRefreshTokenCreateOrUpdateInLogin(User user) {
        RefreshToken refreshToken = refreshTokenCRUDService.loadRefreshTokenFromUserIdx(user.getId());
        if (isNeedToUpdateRefreshTokenInLogin(refreshToken)) {
            String payload = reIssueRefreshToken(refreshToken);
            refreshToken.updatePayload(payload);
            return payload;
        } else if (!isNeedToUpdateRefreshTokenInLogin(refreshToken)) {
            return refreshToken.getPayload();
        }
        return createRefreshToken(user);
    }

    private Boolean isNeedToUpdateRefreshTokenInLogin(RefreshToken refreshToken) {
        Date claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(refreshToken.getPayload())
                    .getBody()
                    .getExpiration();
        } catch (ExpiredJwtException expiredJwtException) {
            return true;
        }

        // Jwt Claims LocalDateTime 으로 형변환
        LocalDateTime tokenExpiredAt = claims
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        // 현재시간 - 7일(초단위) 를 한 피연산자 할당
        LocalDateTime subDetractedDateTime = LocalDateTime.now().plusSeconds(604800);

        // 피연산자 보다 이전 이면 True 반환 및 갱신해줘야함
        return tokenExpiredAt.isBefore(subDetractedDateTime);
    }
}
