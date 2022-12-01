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

    //AccessToken 에서 userIdx 꺼내기
    public Long getId(String token) {

        // Object Type 으로 받는다. (Long 으로 강제 형변환이 안되어서 한번 거쳤다가 Long 으로 )
        // 기존에는 Integer 로 받았지만, Integer 범위를 넘어서는 값이 등장하면 런타임 에러가 발생한다.
        Object id = parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody().get("id");

        // ObjectType 을 Long 으로 변환
        return Long.valueOf(String.valueOf(id));
    }

    //AccessToken loginId 꺼내기
    public String getLoginId(String token) {
        return (String) parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody().get("loginId");
    }

    //AccessToken Role 꺼내기
    public String getUserRole(String token) {
        return (String) parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody().get("role");
    }

    //AccessToken Role 꺼내기
    public String getUserRoleAdvanced(Claims claims) {
        return (String) claims.get("role");
    }

    //AccessToken Restricted 꺼내기
    public boolean getUserIsRestricted(String token) {
        return (boolean) parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody().get("restricted");
    }

    @Transactional
    public String refreshTokenUpdateOrCreate(User user) {

        // 리프레시 토큰이 DB에 있을 때
        if (refreshTokenRepository.findPayLoadByUserIdx(user.getId()).isPresent()) {

            // DB 토큰 꺼내서, 바로 토큰 만료기한 검증, 만료 시 업데이트
            try {
                Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(
                        refreshTokenRepository.findPayLoadByUserIdx(user.getId()).get());
            } catch (ExpiredJwtException exception) {
                return jwtTokenProvider.updateRefreshToken(user.getId());
            }

            // DB에 존재하는 리프레시 토큰 꺼내 담기
            String refreshToken = refreshTokenRepository.findPayLoadByUserIdx(user.getId()).get();

            // 리프레시 토큰이 DB에 있지만, 갱신은 필요로 할 때
            if (jwtTokenValidator.isNeedToUpdateRefreshToken(refreshToken)) {
                // 리프레시 토큰 갱신
                return jwtTokenProvider.updateRefreshToken(user.getId());
            }

            // 리프레시 토큰이 DB에 있고, 갱신을 필요로 하지 않을 때
            else return refreshToken;
        }

        // 리프레시 토큰이 DB에 없을 때
        //리프레시 토큰 신규 생성
        String refreshToken = jwtTokenProvider.createRefreshToken();

        //리프레시 토큰 저장
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userIdx(user.getId())
                        .payload(refreshToken)
                        .build());
        return refreshToken;
    }
}
