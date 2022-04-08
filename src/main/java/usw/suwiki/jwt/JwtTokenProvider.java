package usw.suwiki.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.User;
import usw.suwiki.repository.refreshToken.RefreshTokenRepository;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 3 * 60 * 1000L; // 3분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L; // 7일

    private final UserDetailsService userDetailsService;
    private final JwtTokenResolver jwtTokenResolver;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }


    @Transactional
    //AccessToken 생성
    public String createAccessToken(User user) {
        Date now = new Date();
        Date accessTokenExpireIn = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        //페이로드에 남길 정보들 (Id, loginId, role)
        Claims claims = Jwts.claims();
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

    @Transactional
    //RefreshToken 생성
    public String createRefreshToken() {
        Date now = new Date();

        Date refreshTokenExpireIn = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setHeaderParam("type","JWT")
                .setExpiration(refreshTokenExpireIn)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    @Transactional
    //RefreshToken 업데이트
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

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(String.valueOf(this.jwtTokenResolver.getId(token)));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}