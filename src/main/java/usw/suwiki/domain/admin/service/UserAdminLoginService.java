package usw.suwiki.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.dto.UserRequestDto;
import usw.suwiki.domain.user.entity.User;
import usw.suwiki.domain.user.repository.UserRepository;
import usw.suwiki.domain.user.service.UserCommonService;
import usw.suwiki.global.jwt.JwtTokenProvider;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAdminLoginService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserCommonService userCommonService;
    private final UserRepository userRepository;

    public Map<String, String> adminLogin(UserRequestDto.LoginForm loginForm) {
        Map<String, String> result = new HashMap<>();
        userCommonService.validatePasswordAtUserTable(loginForm.getLoginId(), loginForm.getPassword());
        User user = userCommonService.loadUserFromLoginId(loginForm.getLoginId());
        String accessToken = jwtTokenProvider.createAccessToken(user);
        result.put("AccessToken", accessToken);
        int userCount = userRepository.findAll().size();
        result.put("UserCount", String.valueOf(userCount));

        return result;
    }
}
