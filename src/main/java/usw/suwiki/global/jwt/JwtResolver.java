package usw.suwiki.global.jwt;


import static io.jsonwebtoken.Jwts.parser;

import io.jsonwebtoken.Jwts;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.refreshToken.entity.RefreshToken;
import usw.suwiki.domain.refreshToken.repository.RefreshTokenRepository;
import usw.suwiki.domain.user.user.entity.User;

@Component
@RequiredArgsConstructor
public class JwtResolver {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final JwtValidator jwtValidator;

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    public Long getId(String token) {
        jwtValidator.validateJwt(token);
        Object id = parser()
            .setSigningKey(secretKey.getBytes())
            .parseClaimsJws(token)
            .getBody().get("id");
        return Long.valueOf(String.valueOf(id));
    }

    public String getUserRole(String token) {
        jwtValidator.validateJwt(token);
        return (String) parser()
            .setSigningKey(secretKey.getBytes())
            .parseClaimsJws(token).getBody()
            .get("role");
    }

    public Boolean getUserIsRestricted(String token) {
        jwtValidator.validateJwt(token);
        return (Boolean) parser()
            .setSigningKey(secretKey.getBytes())
            .parseClaimsJws(token)
            .getBody().get("restricted");
    }

    @Transactional
    public String refreshTokenUpdateOrCreate(User user) {
        // 리프레시 토큰을 발급 받은 적 있을 때
        if (refreshTokenRepository.findByUserIdx(user.getId()).isPresent()) {
            return updateOldRefreshToken(user);
        }
        // 리프레시 토큰을 발급받은 적 없을 때
        return createNewRefreshToken(user);
    }

    private String updateOldRefreshToken(User user) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserIdx(user.getId()).get();
        if (isNeedToUpdateRefreshToken(refreshToken)) {
            return jwtProvider.reIssueRefreshToken(refreshToken);
        }
        return refreshToken.getPayload();
    }

    private String createNewRefreshToken(User user) {
        String newRefreshToken = jwtProvider.createRefreshToken();
        refreshTokenRepository.save(
            RefreshToken.builder()
                .userIdx(user.getId())
                .payload(newRefreshToken)
                .build());
        return newRefreshToken;
    }

    private boolean isNeedToUpdateRefreshToken(RefreshToken refreshToken) {
        Date claims = Jwts.parser()
            .setSigningKey(secretKey.getBytes())
            .parseClaimsJws(refreshToken.getPayload())
            .getBody()
            .getExpiration();

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
