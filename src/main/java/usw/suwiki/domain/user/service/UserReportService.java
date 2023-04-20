package usw.suwiki.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.dto.UserRequestDto.EvaluateReportForm;
import usw.suwiki.domain.user.dto.UserRequestDto.ExamReportForm;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.jwt.JwtTokenValidator;

import java.util.HashMap;
import java.util.Map;

import static usw.suwiki.global.exception.ErrorType.USER_RESTRICTED;

@Service
@RequiredArgsConstructor
@Transactional
public class UserReportService {

    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenResolver jwtTokenResolver;
    private final UserService userService;

    public Map<String, Boolean> executeForEvaluatePost(EvaluateReportForm evaluateReportForm, String Authorization) {
        jwtTokenValidator.validateAccessToken(Authorization);
        if (jwtTokenResolver.getUserIsRestricted(Authorization)) throw new AccountException(USER_RESTRICTED);
        Long reportingUser = jwtTokenResolver.getId(Authorization);
        userService.reportEvaluatePost(evaluateReportForm, reportingUser);
        return new HashMap<>() {{
            put("success", true);
        }};
    }

    public Map<String, Boolean> executeForExamPost(ExamReportForm examReportForm, String Authorization) {
        jwtTokenValidator.validateAccessToken(Authorization);
        if (jwtTokenResolver.getUserIsRestricted(Authorization)) throw new AccountException(USER_RESTRICTED);
        Long reportingUser = jwtTokenResolver.getId(Authorization);
        userService.reportExamPost(examReportForm, reportingUser);
        return new HashMap<>() {{
            put("success", true);
        }};
    }
}
