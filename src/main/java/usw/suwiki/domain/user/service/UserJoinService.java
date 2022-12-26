package usw.suwiki.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.blacklistdomain.BlackListService;
import usw.suwiki.domain.user.dto.UserRequestDto.JoinForm;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserJoinService {

    private final BlackListService blackListService;
    private final UserCommonService userCommonService;

    public Map<String, Boolean> execute(JoinForm joinForm) {
        blackListService.joinRequestUserIsBlackList(joinForm.getEmail());
        userCommonService.join(joinForm);

        return new HashMap<>() {{
            put("success", true);
        }};
    }
}
