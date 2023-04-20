package usw.suwiki.domain.user.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.blacklistdomain.BlackListService;
import usw.suwiki.domain.user.restrictinguser.service.RestrictingUserCommonService;
import usw.suwiki.domain.user.user.dto.UserResponseDto.LoadMyBlackListReasonForm;
import usw.suwiki.domain.user.user.dto.UserResponseDto.LoadMyRestrictedReasonForm;
import usw.suwiki.domain.user.user.entity.User;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.jwt.JwtTokenValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserLoadRestrictAndBlackListReasonService {

    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenResolver jwtTokenResolver;
    private final UserService userService;
    private final BlackListService blackListService;
    private final RestrictingUserCommonService restrictingUserCommonService;

    public List<LoadMyBlackListReasonForm> executeForBlackListReason(String Authorization) {
        jwtTokenValidator.validateAccessToken(Authorization);
        User requestUser = userService.loadUserFromUserIdx(jwtTokenResolver.getId(Authorization));
        return blackListService.getBlacklistLog(requestUser.getId());
    }

    public List<LoadMyRestrictedReasonForm> executeForRestrictedReason(String Authorization) {
        jwtTokenValidator.validateAccessToken(Authorization);
        User requestUser = userService.loadUserFromUserIdx(jwtTokenResolver.getId(Authorization));
        return restrictingUserCommonService.loadRestrictedLog(requestUser.getId());
    }
}
