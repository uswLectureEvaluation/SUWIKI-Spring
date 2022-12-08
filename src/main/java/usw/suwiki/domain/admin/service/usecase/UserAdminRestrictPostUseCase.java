package usw.suwiki.domain.admin.service.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.EvaluatePostRestrictForm;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.ExamPostRestrictForm;
import usw.suwiki.domain.admin.service.UserAdminService;
import usw.suwiki.domain.restrictinguser.service.RestrictingUserService;
import usw.suwiki.domain.user.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAdminRestrictPostUseCase {

    private final RestrictingUserService restrictingUserService;
    private final UserService userService;
    private final UserAdminService userAdminService;

    public Map<String, Boolean> restrictEvaluatePost(EvaluatePostRestrictForm evaluatePostRestrictForm) {
        restrictingUserService.addRestrictingTableByEvaluatePost(evaluatePostRestrictForm);
        Long reportingUserIdx = userService.whoIsEvaluateReporting(evaluatePostRestrictForm.getEvaluateIdx());
        Long targetUserIdx = userAdminService.banishEvaluatePost(evaluatePostRestrictForm.getEvaluateIdx());
        userAdminService.plusRestrictCount(targetUserIdx);
        userAdminService.plusReportingUserPoint(reportingUserIdx);

        return new HashMap<>() {{
            put("Success", true);
        }};
    }

    public Map<String, Boolean> restrictExamPost(ExamPostRestrictForm examPostRestrictForm) {
        restrictingUserService.addRestrictingTableByExamPost(examPostRestrictForm);
        Long reportingUserIdx = userService.whoIsExamReporting(examPostRestrictForm.getExamIdx());
        Long targetUserIdx = userAdminService.blacklistOrRestrictAndDeleteExamPost(examPostRestrictForm.getExamIdx());
        userAdminService.plusRestrictCount(targetUserIdx);
        userAdminService.plusReportingUserPoint(reportingUserIdx);

        return new HashMap<>() {{
            put("Success", true);
        }};
    }
}
