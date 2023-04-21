package usw.suwiki.domain.admin.service;

import static usw.suwiki.global.util.apiresponse.ApiResponseFactory.successCapitalFlag;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.EvaluatePostBlacklistForm;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.ExamPostBlacklistForm;
import usw.suwiki.domain.user.user.service.UserService;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAdminBlackListPostService {

    private final Long BANNED_PERIOD = 365L;
    private final UserService userService;
    private final UserAdminService userAdminService;

    public Map<String, Boolean> executeEvaluatePost(
        EvaluatePostBlacklistForm evaluatePostBlacklistForm
    ) {
        Long userIdx = userService.loadEvaluatePostsByIndex(
            evaluatePostBlacklistForm.getEvaluateIdx()
        ).getUser().getId();

        userAdminService.banishEvaluatePost(evaluatePostBlacklistForm.getEvaluateIdx());
        if (userAdminService.isAlreadyBlackList(userIdx)) {
            return successCapitalFlag();
        }

        userAdminService.executeBlacklist(
            userIdx,
            BANNED_PERIOD,
            evaluatePostBlacklistForm.getBannedReason(),
            evaluatePostBlacklistForm.getJudgement()
        );
        userAdminService.plusRestrictCount(userIdx);

        return successCapitalFlag();
    }

    public Map<String, Boolean> executeExamPost(
        ExamPostBlacklistForm examPostBlacklistForm
    ) {
        Long userIdx = userService.loadExamPostsByIndex(
            examPostBlacklistForm.getExamIdx()
        ).getUser().getId();
        userAdminService.blacklistOrRestrictAndDeleteExamPost(
            examPostBlacklistForm.getExamIdx());

        if (userAdminService.isAlreadyBlackList(userIdx)) {
            return successCapitalFlag();
        }

        userAdminService.executeBlacklist(
            userIdx,
            BANNED_PERIOD,
            examPostBlacklistForm.getBannedReason(),
            examPostBlacklistForm.getJudgement()
        );
        userAdminService.plusRestrictCount(userIdx);

        return successCapitalFlag();
    }
}
