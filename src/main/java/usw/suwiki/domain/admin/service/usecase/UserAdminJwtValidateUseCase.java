package usw.suwiki.domain.admin.service.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.jwt.JwtTokenValidator;

import static usw.suwiki.global.exception.ErrorType.USER_RESTRICTED;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAdminJwtValidateUseCase {

    private final JwtTokenResolver jwtTokenResolver;
    private final JwtTokenValidator jwtTokenValidator;

    public void execute(String authorization) {
        jwtTokenValidator.validateAccessToken(authorization);
        if (!jwtTokenResolver.getUserRole(authorization).equals("ADMIN"))
            throw new AccountException(USER_RESTRICTED);
    }
}
