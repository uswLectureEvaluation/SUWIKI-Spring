package usw.suwiki.global.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.refreshToken.RefreshTokenRepository;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L; // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 14 * 24 * 60 * 60 * 1000L; // 14일

    private final RefreshTokenRepository refreshTokenRepository;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }


    //AccessToken 생성
    @Transactional
    public String createAccessToken(User user) {
        Date now = new Date();
        Date accessTokenExpireIn = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        //페이로드에 남길 정보들 (Id, loginId, role, restricted)
        Claims claims = Jwts.claims();
        claims.setSubject(user.getLoginId());
        claims.put("id", user.getId());
        claims.put("loginId", user.getLoginId());
        claims.put("role", user.getRole());
        claims.put("restricted", user.isRestricted());

        // Access Token 생성
        return Jwts.builder()
                .setHeaderParam("type","JWT")
                .setClaims(claims)
                .setExpiration(accessTokenExpireIn)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();

    }

    //RefreshToken 생성
    @Transactional
    public String createRefreshToken() {
        Date now = new Date();

        Date refreshTokenExpireIn = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setHeaderParam("type","JWT")
                .setExpiration(refreshTokenExpireIn)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    //RefreshToken 업데이트
    @Transactional
    public String updateRefreshToken(Long userIdx) {
        Date now = new Date();

        Date refreshTokenExpireIn = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        String newRefreshToken =  Jwts.builder()
                .setHeaderParam("type","JWT")
                .setExpiration(refreshTokenExpireIn)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();

        refreshTokenRepository.updatePayload(newRefreshToken, userIdx);

        return newRefreshToken;
    }
}