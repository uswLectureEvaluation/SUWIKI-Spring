package usw.suwiki.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.dto.UserRequestDto.UserQuitForm;
import usw.suwiki.domain.user.entity.User;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtTokenValidator;

import java.util.HashMap;
import java.util.Map;

import static usw.suwiki.global.exception.ErrorType.USER_NOT_EXISTS;

@Service
@RequiredArgsConstructor
@Transactional
public class UserQuitService {

    private final JwtTokenValidator jwtTokenValidator;
    private final UserCommonService userCommonService;
    private final QuitRequestUserService quitRequestUserService;

    public Map<String, Boolean> execute(UserQuitForm userQuitForm, String Authorization) {
        jwtTokenValidator.validateAccessToken(Authorization);
        if (!userCommonService.validatePasswordAtUserTable(userQuitForm.getLoginId(), userQuitForm.getPassword()))
            throw new AccountException(USER_NOT_EXISTS);
        User theUserRequestedQuit = userCommonService.loadUserFromLoginId(userQuitForm.getLoginId());
        quitRequestUserService.waitQuit(theUserRequestedQuit.getId());
        quitRequestUserService.requestQuitDateStamp(theUserRequestedQuit);
        return new HashMap<>() {{
            put("success", true);
        }};
    }
}
