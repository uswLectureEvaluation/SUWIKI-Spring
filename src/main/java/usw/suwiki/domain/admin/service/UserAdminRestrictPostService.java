package usw.suwiki.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.EvaluatePostRestrictForm;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.ExamPostRestrictForm;
import usw.suwiki.domain.user.restrictinguser.service.RestrictingUserAddRestrictingService;
import usw.suwiki.domain.user.user.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAdminRestrictPostService {
    private final UserService userService;
    private final UserAdminService userAdminService;
    private final RestrictingUserAddRestrictingService restrictingUserAddRestrictingService;

    public Map<String, Boolean> restrictEvaluatePost(EvaluatePostRestrictForm evaluatePostRestrictForm) {
        restrictingUserAddRestrictingService.executeEvaluatePost(evaluatePostRestrictForm);
        Long reportingUserIdx = userService.whoIsEvaluateReporting(evaluatePostRestrictForm.getEvaluateIdx());
        Long targetUserIdx = userAdminService.banishEvaluatePost(evaluatePostRestrictForm.getEvaluateIdx());
        userAdminService.plusRestrictCount(targetUserIdx);
        userAdminService.plusReportingUserPoint(reportingUserIdx);

        return new HashMap<>() {{
            put("Success", true);
        }};
    }

    public Map<String, Boolean> restrictExamPost(ExamPostRestrictForm examPostRestrictForm) {
        restrictingUserAddRestrictingService.executeExamPost(examPostRestrictForm);
        Long reportingUserIdx = userService.whoIsExamReporting(examPostRestrictForm.getExamIdx());
        Long targetUserIdx = userAdminService.blacklistOrRestrictAndDeleteExamPost(examPostRestrictForm.getExamIdx());
        userAdminService.plusRestrictCount(targetUserIdx);
        userAdminService.plusReportingUserPoint(reportingUserIdx);

        return new HashMap<>() {{
            put("Success", true);
        }};
    }
}
