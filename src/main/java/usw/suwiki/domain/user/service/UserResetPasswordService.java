package usw.suwiki.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.dto.UserRequestDto.EditMyPasswordForm;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.jwt.JwtTokenValidator;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserResetPasswordService {

    private final UserService userService;
    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenResolver jwtTokenResolver;

    public Map<String, Boolean> execute(String Authorization, EditMyPasswordForm editMyPasswordForm) {
        jwtTokenValidator.validateAccessToken(Authorization);
        userService.validatePasswordAtEditPassword(
                jwtTokenResolver.getLoginId(Authorization), editMyPasswordForm.getPrePassword());
        userService.compareNewPasswordVersusPrePassword(
                jwtTokenResolver.getLoginId(Authorization), editMyPasswordForm.getNewPassword());
        userService.editMyPassword(editMyPasswordForm, Authorization);

        return new HashMap<>() {{
            put("success", true);
        }};
    }
}