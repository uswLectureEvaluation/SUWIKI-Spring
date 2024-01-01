package usw.suwiki.global.jwt;

import static usw.suwiki.global.exception.ExceptionType.LOGIN_REQUIRED;
import static usw.suwiki.global.exception.ExceptionType.TOKEN_IS_BROKEN;
import static usw.suwiki.global.exception.ExceptionType.TOKEN_IS_EXPIRED;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.refreshtoken.RefreshToken;
import usw.suwiki.domain.refreshtoken.service.RefreshTokenCRUDService;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.global.exception.errortype.AccountException;

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
    public String reissueRefreshToken(String payload) {
        // TODO: Security 공부하면서 정말 필요한 로직만 넣자.
        // TODO: 레디스 캐싱 성능 개선

        // RefreshToken 엔티티 조회
        RefreshToken refreshToken = refreshTokenCRUDService.loadRefreshTokenFromPayload(payload);

        // TODO: 애초에 payload로 찾은건데 여기서 비교할 필요가 있나?
        if (!refreshToken.getPayload().equals(payload)) {
            throw new AccountException(TOKEN_IS_BROKEN);
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
            throw new AccountException(LOGIN_REQUIRED);
        } catch (ExpiredJwtException exception) {
            throw new AccountException(TOKEN_IS_EXPIRED);
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
            throw new AccountException(TOKEN_IS_EXPIRED);
        }
    }

    // TODO: 만료시한까지 7일 이하 남았을 때만 -> 1/4 남았을 때만.
    private static boolean isRefreshTokenNotExpired(Date date) {   // 기존 만료일자 날짜 비교 로직
        // Jwt Claims LocalDateTime 으로 형변환
        LocalDateTime tokenExpiredAt = date
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        System.out.println("tokenExpiredAt = " + tokenExpiredAt);
        System.out.println("LocalDateTime.now() = " + LocalDateTime.now());

        // 만료 시간 > 현재 시간 => 정상

        // 현재시간 - 7일(초단위) 를 한 피연산자 할당
        LocalDateTime subDetractedDateTime = LocalDateTime.now().plusSeconds(604800);
        System.out.println("subDetractedDateTime = " + subDetractedDateTime);

        System.out.println(
                "tokenExpiredAt.isBefore(subDetractedDateTime) = " + tokenExpiredAt.isBefore(subDetractedDateTime));
        // 피연산자 보다 이전 이면 True 반환 및 갱신해줘야함
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
