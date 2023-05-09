package usw.suwiki.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;
import java.util.Date;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.refreshToken.entity.RefreshToken;
import usw.suwiki.domain.refreshToken.repository.RefreshTokenRepository;
import usw.suwiki.domain.user.user.User;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    private static final Long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L; // 30분
    private static final Long REFRESH_TOKEN_EXPIRE_TIME = 270 * 24 * 60 * 60 * 1000L; // 270일 -> 9개월
//    private static final Long ACCESS_TOKEN_EXPIRE_TIME = 1 * 1000L; // 1초
//    private static final Long REFRESH_TOKEN_EXPIRE_TIME = 5 * 60 * 1000L; // 5분

    private final RefreshTokenRepository refreshTokenRepository;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createAccessToken(User user) {
        return buildAccessToken(
            setAccessTokenClaimsByUser(user),
            new Date(new Date().getTime() + ACCESS_TOKEN_EXPIRE_TIME)
        );
    }

    public String createRefreshToken(User user) {
        String buildRefreshToken = buildRefreshToken(
            new Date(new Date().getTime() + REFRESH_TOKEN_EXPIRE_TIME)
        );

        refreshTokenRepository.save(RefreshToken.builder()
            .userIdx(user.getId())
            .payload(buildRefreshToken)
            .build());

        return buildRefreshToken;
    }

    @Transactional
    public String reIssueRefreshToken(RefreshToken refreshToken) {
        refreshToken.updatePayload(
            buildRefreshToken(
                new Date(new Date().getTime() + REFRESH_TOKEN_EXPIRE_TIME)
            )
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
}