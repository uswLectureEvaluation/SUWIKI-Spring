package usw.suwiki.global.jwt;


import static io.jsonwebtoken.Jwts.parser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.refreshToken.entity.RefreshToken;
import usw.suwiki.domain.refreshToken.repository.RefreshTokenRepository;
import usw.suwiki.domain.user.entity.User;

@Component
@RequiredArgsConstructor
public class JwtTokenResolver {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    public Long getId(String token) {
        Object id = parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody()
            .get("id");
        return Long.valueOf(String.valueOf(id));
    }

    public String getLoginId(String token) {
        return (String) parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody()
            .get("loginId");
    }

    public String getUserRole(String token) {
        return (String) parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody()
            .get("role");
    }

    public String getUserRoleAdvanced(Claims claims) {
        return (String) claims.get("role");
    }

    public boolean getUserIsRestricted(String token) {
        return (boolean) parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token)
            .getBody().get("restricted");
    }

    @Transactional
    public String refreshTokenUpdateOrCreate(User user) {
        if (refreshTokenRepository.loadPayloadByUserIdx(user.getId()) != null) {
            try {
                Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(
                        refreshTokenRepository.loadPayloadByUserIdx(user.getId()));
            } catch (ExpiredJwtException exception) {
                return jwtTokenProvider.updateRefreshToken(user.getId());
            }
            String refreshToken = refreshTokenRepository.loadPayloadByUserIdx(user.getId());
            if (jwtTokenValidator.isNeedToUpdateRefreshToken(refreshToken)) {
                return jwtTokenProvider.updateRefreshToken(user.getId());
            } else {
                return refreshToken;
            }
        }
        String refreshToken = jwtTokenProvider.createRefreshToken();
        refreshTokenRepository.save(
            RefreshToken.builder()
                .userIdx(user.getId())
                .payload(refreshToken)
                .build());
        return refreshToken;
    }
}
