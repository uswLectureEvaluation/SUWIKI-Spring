package usw.suwiki.global.jwt;

import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import usw.suwiki.exception.errortype.AccountException;
import usw.suwiki.exception.ErrorType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Log4j2
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final String KEY_ROLES = "role";
    private final byte[] secretKeyByte;


    private final JwtTokenResolver jwtTokenResolver;

    public JwtAuthenticationProvider(@Value("${spring.jwt.secret-key}") String secretKey, JwtTokenResolver jwtTokenResolver) {
        this.secretKeyByte = secretKey.getBytes();
        this.jwtTokenResolver = jwtTokenResolver;
    }

    private Collection<? extends GrantedAuthority> createGrantedAuthorities(Claims claims) {
        String roles = jwtTokenResolver.getUserRoleAdvanced(claims);
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(() -> roles);
        return grantedAuthorities;
    }

    /**
     * JwtParser.parse method can throw below exception, so you should catch and do something.
     * MalformedJwtException – if the specified JWT was incorrectly constructed (and therefore invalid). Invalid JWTs should not be trusted and should be discarded.
     * SignatureException – if a JWS signature was discovered, but could not be verified. JWTs that fail signature validation should not be trusted and should be discarded.
     * ExpiredJwtException – if the specified JWT is a Claims JWT and the Claims has an expiration time before the time this method is invoked.
     * IllegalArgumentException – if the specified string is null or empty or only whitespace.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(secretKeyByte).parseClaimsJws(((JwtAuthenticationToken) authentication).getJsonWebToken()).getBody();
        } catch (SignatureException | IllegalArgumentException | MalformedJwtException |
                 ExpiredJwtException signatureException) {
            throw new AccountException(ErrorType.NOT_EXISTS_LECTURE);
        }

        return new JwtAuthenticationToken(claims.getSubject(), "", createGrantedAuthorities(claims));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
