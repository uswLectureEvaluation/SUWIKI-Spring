package usw.suwiki.domain.user.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.user.dto.UserRequestDto.EvaluateReportForm;
import usw.suwiki.domain.user.user.dto.UserRequestDto.ExamReportForm;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtResolver;
import usw.suwiki.global.jwt.JwtValidator;

import java.util.Map;

import static usw.suwiki.global.exception.ExceptionType.USER_RESTRICTED;
import static usw.suwiki.global.util.apiresponse.ApiResponseFactory.successFlag;

@Service
@RequiredArgsConstructor
@Transactional
public class UserReportService {

    private final JwtValidator jwtValidator;
    private final JwtResolver jwtResolver;
    private final UserService userService;

    public Map<String, Boolean> executeForEvaluatePost(EvaluateReportForm evaluateReportForm, String Authorization) {
        jwtValidator.validateJwt(Authorization);
        if (jwtResolver.getUserIsRestricted(Authorization)) throw new AccountException(USER_RESTRICTED);
        Long reportingUser = jwtResolver.getId(Authorization);
        userService.reportEvaluatePost(evaluateReportForm, reportingUser);
        return successFlag();
    }

    public Map<String, Boolean> executeForExamPost(ExamReportForm examReportForm, String Authorization) {
        jwtValidator.validateJwt(Authorization);
        if (jwtResolver.getUserIsRestricted(Authorization)) throw new AccountException(USER_RESTRICTED);
        Long reportingUser = jwtResolver.getId(Authorization);
        userService.reportExamPost(examReportForm, reportingUser);
        return successFlag();
    }
}
