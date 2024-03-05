package usw.suwiki.auth.jwt;

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
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.domain.refreshtoken.RefreshToken;
import usw.suwiki.domain.refreshtoken.service.RefreshTokenCRUDService;
import usw.suwiki.domain.user.user.User;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAgent {

    @Value("${spring.secret-key}")
    private String key;

    @Value("${jwt.access-duration}")
    public long accessTokenExpireTime;

    @Value("${jwt.refresh-duration}")
    public long refreshTokenExpireTime;

    private final RefreshTokenCRUDService refreshTokenCRUDService;


    @Transactional
    public String provideRefreshTokenInLogin(User user) {
        Optional<RefreshToken> wrappedRefreshToken =
            refreshTokenCRUDService.loadRefreshTokenFromUserIdx(user.getId());

        if (wrappedRefreshToken.isEmpty()) {
            return createRefreshToken(user);
        }

        RefreshToken refreshToken = wrappedRefreshToken.get();
        if (isRefreshTokenExpired(refreshToken.getPayload())) {
            String payload = reIssueRefreshToken(refreshToken);
            wrappedRefreshToken.get().updatePayload(payload);
            return payload;
        }
        return refreshToken.getPayload();
    }

    @Transactional
    public String reissueRefreshToken(String payload) {
        // TODO: Security 공부하면서 정말 필요한 로직만 넣자.
        // TODO: 레디스 캐싱 성능 개선

        RefreshToken refreshToken = refreshTokenCRUDService.loadRefreshTokenFromPayload(payload);

        // TODO: 애초에 payload로 찾은건데 여기서 비교할 필요가 있나?
        if (!refreshToken.getPayload().equals(payload)) {
            throw new AccountException(ExceptionType.TOKEN_IS_BROKEN);
        }

        Claims body = resolveBodyFromRefreshToken(payload);

        // TODO: isRefreshTokenNotExpired 리팩토링 후 추가
        String newPayload = reIssueRefreshToken(refreshToken);
        refreshToken.updatePayload(newPayload);
        return newPayload;
    }

    @Transactional
    public String reIssueRefreshToken(RefreshToken refreshToken) {
        refreshToken.updatePayload(
            buildRefreshToken(new Date(new Date().getTime() + refreshTokenExpireTime))
        );
        return refreshToken.getPayload();
    }

    public void validateJwt(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token);
        } catch (MalformedJwtException | IllegalArgumentException ex) {
            throw new AccountException(ExceptionType.LOGIN_REQUIRED);
        } catch (ExpiredJwtException exception) {
            throw new AccountException(ExceptionType.TOKEN_IS_EXPIRED);
        }
    }

    public String createAccessToken(User user) {
        return buildAccessToken(
            setAccessTokenClaimsByUser(user),
            new Date(new Date().getTime() + accessTokenExpireTime)
        );
    }

    public String createRefreshToken(User user) {
        String buildRefreshToken = buildRefreshToken(new Date(new Date().getTime() + refreshTokenExpireTime));
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

    private Claims resolveBodyFromRefreshToken(String refreshToken) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();
        } catch (ExpiredJwtException expiredJwtException) {
            throw new AccountException(ExceptionType.TOKEN_IS_EXPIRED);
        }
    }

    // TODO: 만료시한까지 7일 이하 남았을 때만 -> 1/4 남았을 때만.
    private static boolean isRefreshTokenNotExpired(Date date) {
        LocalDateTime tokenExpiredAt = date
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();

        LocalDateTime subDetractedDateTime = LocalDateTime.now().plusSeconds(604800);
        return tokenExpiredAt.isBefore(LocalDateTime.now());
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

    // TODO: body 값 정보 추가하기 (type:ac,re , subject:유저 식별자)
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
