package usw.suwiki.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.refreshtoken.RefreshToken;
import usw.suwiki.domain.refreshtoken.service.RefreshTokenCRUDService;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.global.exception.errortype.AccountException;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static usw.suwiki.global.exception.ExceptionType.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAgent {
    @Value("${spring.secret-key}")
    private String key;

    // private static final Long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L; // 30분
    // private static final Long REFRESH_TOKEN_EXPIRE_TIME = 270 * 24 * 60 * 60 * 1000L; // 270일 -> 9개월

    private static final Long ACCESS_TOKEN_EXPIRE_TIME = 1 * 60 * 1000L; // 1분
    private static final Long REFRESH_TOKEN_EXPIRE_TIME = 3 * 60 * 1000L; // 3분

    private final RefreshTokenCRUDService refreshTokenCRUDService;


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
            String validatedPayload = refreshToken.get().getPayload();
            if (validatedPayload.equals(payload)) {
                if (isRefreshTokenExpired(payload)) {
                    log.error(LocalDateTime.now() + " - 리프레시 토큰이 만료되었습니다.");
                    throw new AccountException(TOKEN_IS_EXPIRED);
                } else if (isTokenNotExpiredButNeededReIssue(payload)) {
                    String newPayload = reIssueRefreshToken(refreshToken.get());
                    refreshToken.get().updatePayload(newPayload);
                    return newPayload;
                }
            }
        }
        log.error(LocalDateTime.now() + " - 토큰이 DB와 일치하지 않습니다.");
        throw new AccountException(TOKEN_IS_BROKEN);
    }

    @Transactional
    public String reIssueRefreshToken(RefreshToken refreshToken) {
        refreshToken.updatePayload(
                buildRefreshToken(new Date(new Date().getTime() + REFRESH_TOKEN_EXPIRE_TIME))
        );
        return refreshToken.getPayload();
    }

    public void validateJwt(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()).build()
                    .parseClaimsJws(token);
        } catch (MalformedJwtException | IllegalArgumentException ex) {
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

    private Boolean isRefreshTokenExpired(String payload) {
        Date date;
        try {
            date = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(payload)
                    .getBody().getExpiration();
        } catch (ExpiredJwtException expiredJwtException) {
            return true;
        }
        return false;
    }

    private boolean isTokenNotExpiredButNeededReIssue(String payload) {
        Date date;
        try {
            date = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(payload)
                    .getBody().getExpiration();
        } catch (ExpiredJwtException expiredJwtException) {
            log.error(LocalDateTime.now() + " - 리프레시 토큰이 만료되었습니다.");
            throw new AccountException(TOKEN_IS_EXPIRED);
        }

        // Jwt Claims LocalDateTime 으로 형변환
        LocalDateTime tokenExpiredAt = date
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        // 현재시간 - 7일(초단위) 를 한 피연산자 할당
        LocalDateTime subDetractedDateTime = LocalDateTime.now().plusSeconds(604800);

        // 피연산자 보다 이전 이면 True 반환 및 갱신해줘야함
        return tokenExpiredAt.isBefore(subDetractedDateTime);
    }

    private Key getSigningKey() {
        final byte[] keyBytes = Decoders.BASE64.decode(this.key);
        return Keys.hmacShaKeyFor(keyBytes);
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
}
