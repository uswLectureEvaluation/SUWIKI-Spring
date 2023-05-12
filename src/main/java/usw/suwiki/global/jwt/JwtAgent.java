package usw.suwiki.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
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

import static usw.suwiki.global.exception.ExceptionType.LOGIN_REQUIRED;
import static usw.suwiki.global.exception.ExceptionType.TOKEN_IS_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class JwtAgent {

//    @Value("${spring.jwt.secret-key}")
//    private String secretKey;

    private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    private static final Long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L; // 30분
    private static final Long REFRESH_TOKEN_EXPIRE_TIME = 270 * 24 * 60 * 60 * 1000L; // 270일 -> 9개월
//    private static final Long ACCESS_TOKEN_EXPIRE_TIME = 1 * 1000L; // 1초
//    private static final Long REFRESH_TOKEN_EXPIRE_TIME = 5 * 60 * 1000L; // 5분

    private final RefreshTokenCRUDService refreshTokenCRUDService;

//    @PostConstruct
//    protected void init() {
//        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
//    }

    public void validateJwt(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key).build()
                    .parseClaimsJws(token);
        } catch (MalformedJwtException | IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            throw new AccountException(LOGIN_REQUIRED);
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
                .signWith(key)
                .setHeaderParam("type", "JWT")
                .setClaims(claims)
                .setExpiration(accessTokenExpireIn)
                .compact();
    }

    private String buildRefreshToken(Date refreshTokenExpireIn) {
        return Jwts.builder()
                .signWith(key)
                .setHeaderParam("type", "JWT")
                .setExpiration(refreshTokenExpireIn)
                .compact();
    }

    public Long getId(String token) {
        validateJwt(token);
        Object id = Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody().get("id");
        return Long.valueOf(String.valueOf(id));
    }

    public String getUserRole(String token) {
        validateJwt(token);
        return (String) Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody().get("role");
    }

    public Boolean getUserIsRestricted(String token) {
        validateJwt(token);
        return (Boolean) Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody().get("restricted");
    }

    /**
     * 로그인 요청 시 리프레시 토큰을 갱신해 줘야 할지 판단한다.
     */
    @Transactional
    public String judgementRefreshTokenCreateOrUpdateInLogin(User user) {
        Optional<RefreshToken> wrappedRefreshToken =
                refreshTokenCRUDService.loadRefreshTokenFromUserIdx(user.getId());
        if (wrappedRefreshToken.isEmpty()) {
            return createRefreshToken(user);
        }
        RefreshToken refreshToken = wrappedRefreshToken.get();
        if (isNeedToUpdateRefreshTokenInLogin(refreshToken)) {
            String payload = reIssueRefreshToken(refreshToken);
            wrappedRefreshToken.get().updatePayload(payload);
            return payload;
        }
        return refreshToken.getPayload();
    }

    private Boolean isNeedToUpdateRefreshTokenInLogin(RefreshToken refreshToken) {
        Date claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(key).build()
                    .parseClaimsJws(refreshToken.getPayload())
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
