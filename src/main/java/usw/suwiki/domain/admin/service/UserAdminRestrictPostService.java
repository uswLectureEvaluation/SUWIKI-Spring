package usw.suwiki.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.EvaluatePostRestrictForm;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.ExamPostRestrictForm;
import usw.suwiki.domain.restrictinguser.service.RestrictingUserAddRestrictingService;
import usw.suwiki.domain.user.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAdminRestrictPostService {
    private final UserService userService;
    private final UserAdminCommonService userAdminCommonService;
    private final RestrictingUserAddRestrictingService restrictingUserAddRestrictingService;

    public Map<String, Boolean> restrictEvaluatePost(EvaluatePostRestrictForm evaluatePostRestrictForm) {
        restrictingUserAddRestrictingService.executeEvaluatePost(evaluatePostRestrictForm);
        Long reportingUserIdx = userService.whoIsEvaluateReporting(evaluatePostRestrictForm.getEvaluateIdx());
        Long targetUserIdx = userAdminCommonService.banishEvaluatePost(evaluatePostRestrictForm.getEvaluateIdx());
        userAdminCommonService.plusRestrictCount(targetUserIdx);
        userAdminCommonService.plusReportingUserPoint(reportingUserIdx);

        return new HashMap<>() {{
            put("Success", true);
        }};
    }

    public Map<String, Boolean> restrictExamPost(ExamPostRestrictForm examPostRestrictForm) {
        restrictingUserAddRestrictingService.executeExamPost(examPostRestrictForm);
        Long reportingUserIdx = userService.whoIsExamReporting(examPostRestrictForm.getExamIdx());
        Long targetUserIdx = userAdminCommonService.blacklistOrRestrictAndDeleteExamPost(examPostRestrictForm.getExamIdx());
        userAdminCommonService.plusRestrictCount(targetUserIdx);
        userAdminCommonService.plusReportingUserPoint(reportingUserIdx);

        return new HashMap<>() {{
            put("Success", true);
        }};
    }
}
