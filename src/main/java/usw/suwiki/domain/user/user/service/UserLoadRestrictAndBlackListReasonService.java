package usw.suwiki.domain.user.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.blacklistdomain.BlackListService;
import usw.suwiki.domain.user.restrictinguser.service.RestrictingUserCommonService;
import usw.suwiki.domain.user.user.dto.UserResponseDto.LoadMyBlackListReasonForm;
import usw.suwiki.domain.user.user.dto.UserResponseDto.LoadMyRestrictedReasonForm;
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
    private final RestrictingUserCommonService restrictingUserCommonService;

    public List<LoadMyBlackListReasonForm> executeForBlackListReason(String Authorization) {
        jwtValidator.validateJwt(Authorization);
        User requestUser = userService.loadUserFromUserIdx(jwtResolver.getId(Authorization));
        return blackListService.getBlacklistLog(requestUser.getId());
    }

    public List<LoadMyRestrictedReasonForm> executeForRestrictedReason(String Authorization) {
        jwtValidator.validateJwt(Authorization);
        User requestUser = userService.loadUserFromUserIdx(jwtResolver.getId(Authorization));
        return restrictingUserCommonService.loadRestrictedLog(requestUser.getId());
    }
}
