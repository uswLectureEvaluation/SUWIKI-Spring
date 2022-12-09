package usw.suwiki.domain.user.service.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.dto.UserRequestDto.FindIdForm;
import usw.suwiki.domain.user.service.UserService;
import usw.suwiki.global.exception.errortype.AccountException;

import java.util.HashMap;
import java.util.Map;

import static usw.suwiki.global.exception.ErrorType.USER_NOT_EXISTS;

@Service
@RequiredArgsConstructor
@Transactional
public class UserFindIdUseCase {

    private final UserService userService;

    public Map<String, Boolean> execute(FindIdForm findIdForm) {
        if (!userService.sendEmailFindId(findIdForm)) throw new AccountException(USER_NOT_EXISTS);

        return new HashMap<>() {{
            put("success", true);
        }};
    }
}