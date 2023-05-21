package usw.suwiki.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.refreshToken.RefreshToken;
import usw.suwiki.domain.refreshToken.service.RefreshTokenCRUDService;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.global.exception.errortype.AccountException;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static usw.suwiki.global.exception.ExceptionType.*;

@Component
@RequiredArgsConstructor
public class JwtAgent {
    @Value("${spring.secret-key}")
    private final String key;

    // private static final Long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L; // 30분
    private static final Long ACCESS_TOKEN_EXPIRE_TIME = 1 * 10 * 1000L; // 10초
    private static final Long REFRESH_TOKEN_EXPIRE_TIME = 270 * 24 * 60 * 60 * 1000L; // 270일 -> 9개월
//    private static final Long ACCESS_TOKEN_EXPIRE_TIME = 1 * 1000L; // 1초
//    private static final Long REFRESH_TOKEN_EXPIRE_TIME = 5 * 60 * 1000L; // 5분

    private final RefreshTokenCRUDService refreshTokenCRUDService;

    private Key getSigningKey() {
        final byte[] keyBytes = Decoders.BASE64.decode(this.key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public void validateJwt(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key).build()
                    .parseClaimsJws(token);
        } catch (MalformedJwtException | IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            throw new AccountException(LOGIN_REQUIRED);
        } catch (ExpiredJwtException exception) {
            throw new AccountException(TOKEN_IS_EXPIRED);
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
                .signWith(getSigningKey())
                .setHeaderParam("type", "JWT")
                .setClaims(claims)
                .setExpiration(accessTokenExpireIn)
                .compact();
    }

    private String buildRefreshToken(Date refreshTokenExpireIn) {
        return Jwts.builder()
                .signWith(getSigningKey())
                .setHeaderParam("type", "JWT")
                .setExpiration(refreshTokenExpireIn)
                .compact();
    }

    public Long getId(String token) {
        validateJwt(token);
        Object id = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody().get("id");
        return Long.valueOf(String.valueOf(id));
    }

    public String getUserRole(String token) {
        validateJwt(token);
        return (String) Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody().get("role");
    }

    public Boolean getUserIsRestricted(String token) {
        validateJwt(token);
        return (Boolean) Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody().get("restricted");
    }

    @Transactional
    public String provideRefreshTokenInLogin(User user) {
        Optional<RefreshToken> wrappedRefreshToken =
                refreshTokenCRUDService.loadRefreshTokenFromUserIdx(user.getId());
        // 생애 첫 로그인 시 리프레시 토큰 신규 발급
        if (wrappedRefreshToken.isEmpty()) {
            return createRefreshToken(user);
        }

        // 그렇지 않으면 DB에서 꺼내기
        RefreshToken refreshToken = wrappedRefreshToken.get();
        if (isRefreshTokenExpired(refreshToken.getPayload())) {
            String payload = reIssueRefreshToken(refreshToken);
            wrappedRefreshToken.get().updatePayload(payload);
            return payload;
        }
        return refreshToken.getPayload();
    }

    @Transactional
    public String refreshTokenRefresh(String payload) {
        Optional<RefreshToken> refreshToken = refreshTokenCRUDService.loadRefreshTokenFromPayload(payload);
        if (refreshToken.isPresent()) {
            if (refreshToken.get().getPayload().equals(payload)) {
                if (!isRefreshTokenExpired(payload)) {
                    String newPayload = reIssueRefreshToken(refreshToken.get());
                    refreshToken.get().updatePayload(newPayload);
                    return newPayload;
                }
                throw new AccountException(TOKEN_IS_EXPIRED);
            }
        }
        throw new AccountException(TOKEN_IS_BROKEN);
    }

    private Boolean isRefreshTokenExpired(String refreshToken) {
        Date claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody().getExpiration();
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
