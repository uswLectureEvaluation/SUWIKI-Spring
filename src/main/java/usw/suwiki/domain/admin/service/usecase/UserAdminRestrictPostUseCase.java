package usw.suwiki.domain.admin.service.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.EvaluatePostRestrictForm;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.ExamPostRestrictForm;
import usw.suwiki.domain.admin.service.UserAdminService;
import usw.suwiki.domain.restrictinguser.service.usecase.RestrictingUserAddRestrictingUserUseCase;
import usw.suwiki.domain.user.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAdminRestrictPostUseCase {
    private final UserService userService;
    private final UserAdminService userAdminService;
    private final RestrictingUserAddRestrictingUserUseCase restrictingUserAddRestrictingUserUseCase;

    public Map<String, Boolean> restrictEvaluatePost(EvaluatePostRestrictForm evaluatePostRestrictForm) {
        restrictingUserAddRestrictingUserUseCase.executeEvaluatePost(evaluatePostRestrictForm);
        Long reportingUserIdx = userService.whoIsEvaluateReporting(evaluatePostRestrictForm.getEvaluateIdx());
        Long targetUserIdx = userAdminService.banishEvaluatePost(evaluatePostRestrictForm.getEvaluateIdx());
        userAdminService.addRestrictCount(targetUserIdx);
        userAdminService.addReportingUserPoint(reportingUserIdx);

        return new HashMap<>() {{
            put("Success", true);
        }};
    }

    public Map<String, Boolean> restrictExamPost(ExamPostRestrictForm examPostRestrictForm) {
        restrictingUserAddRestrictingUserUseCase.executeExamPost(examPostRestrictForm);
        Long reportingUserIdx = userService.whoIsExamReporting(examPostRestrictForm.getExamIdx());
        Long targetUserIdx = userAdminService.blacklistOrRestrictAndDeleteExamPost(examPostRestrictForm.getExamIdx());
        userAdminService.addRestrictCount(targetUserIdx);
        userAdminService.addReportingUserPoint(reportingUserIdx);

        return new HashMap<>() {{
            put("Success", true);
        }};
    }
}
