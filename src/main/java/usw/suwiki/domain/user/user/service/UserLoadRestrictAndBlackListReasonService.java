package usw.suwiki.domain.user.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.blacklistdomain.BlackListService;
import usw.suwiki.domain.admin.restrictinguser.RestrictingUserService;
import usw.suwiki.domain.user.user.dto.UserResponseDto.LoadMyBlackListReasonResponseForm;
import usw.suwiki.domain.user.user.dto.UserResponseDto.LoadMyRestrictedReasonResponseForm;
import usw.suwiki.domain.user.user.entity.User;
import usw.suwiki.global.jwt.JwtResolver;
import usw.suwiki.global.jwt.JwtValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserLoadRestrictAndBlackListReasonService {

    private final JwtValidator jwtValidator;
    private final JwtResolver jwtResolver;
    private final UserService userService;
    private final BlackListService blackListService;
    private final RestrictingUserService restrictingUserService;

    public List<LoadMyBlackListReasonResponseForm> executeForBlackListReason(String Authorization) {
        jwtValidator.validateJwt(Authorization);
        User requestUser = userService.loadUserFromUserIdx(jwtResolver.getId(Authorization));
        return blackListService.loadBlacklistLog(requestUser.getId());
    }

    public List<LoadMyRestrictedReasonResponseForm> executeForRestrictedReason(String Authorization) {
        jwtValidator.validateJwt(Authorization);
        User requestUser = userService.loadUserFromUserIdx(jwtResolver.getId(Authorization));
        return restrictingUserService.loadRestrictedLog(requestUser.getId());
    }
}
