package usw.suwiki.domain.user.service.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.blacklistdomain.BlackListService;
import usw.suwiki.domain.user.dto.UserRequestDto.JoinForm;
import usw.suwiki.domain.user.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserJoinUseCase {

    private final BlackListService blackListService;
    private final UserService userService;

    public Map<String, Boolean> execute(JoinForm joinForm) {
        blackListService.joinRequestUserIsBlackList(joinForm.getEmail());
        userService.join(joinForm);

        return new HashMap<>() {{
            put("success", true);
        }};
    }
}
