package usw.suwiki.global.jwt;


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

import static io.jsonwebtoken.Jwts.parser;

@Component
@RequiredArgsConstructor
public class JwtTokenResolver {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    public Long getId(String token) {
        // Object Type 으로 받는다. (Long 으로 강제 형변환이 안되어서 한번 거쳤다가 Long 으로 )
        // 기존에는 Integer 로 받았지만, Integer 범위를 넘어서는 값이 등장하면 런타임 에러가 발생한다.
        Object id = parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody().get("id");
        return Long.valueOf(String.valueOf(id));
    }

    public String getLoginId(String token) {
        return (String) parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody().get("loginId");
    }

    public String getUserRole(String token) {
        return (String) parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody().get("role");
    }

    public String getUserRoleAdvanced(Claims claims) {
        return (String) claims.get("role");
    }

    public boolean getUserIsRestricted(String token) {
        return (boolean) parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody().get("restricted");
    }

    @Transactional
    public String refreshTokenUpdateOrCreate(User user) {
        if (refreshTokenRepository.findPayLoadByUserIdx(user.getId()).isPresent()) {
            try {
                Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(
                        refreshTokenRepository.findPayLoadByUserIdx(user.getId()).get());
            } catch (ExpiredJwtException exception) {
                return jwtTokenProvider.updateRefreshToken(user.getId());
            }
            String refreshToken = refreshTokenRepository.findPayLoadByUserIdx(user.getId()).get();
            if (jwtTokenValidator.isNeedToUpdateRefreshToken(refreshToken)) {
                return jwtTokenProvider.updateRefreshToken(user.getId());
            } else return refreshToken;
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
