package usw.suwiki.domain.admin.service.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.EvaluatePostBlacklistForm;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.ExamPostBlacklistForm;
import usw.suwiki.domain.admin.service.UserAdminService;
import usw.suwiki.domain.user.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAdminBlackListPostUseCase {

    private final UserService userService;
    private final UserAdminService userAdminService;

    public Map<String, Boolean> executeEvaluatePost(EvaluatePostBlacklistForm evaluatePostBlacklistForm) {
        Long userIdx = userService.loadEvaluatePostsByIndex(evaluatePostBlacklistForm.getEvaluateIdx()).getUser().getId();
        userAdminService.banishEvaluatePost(evaluatePostBlacklistForm.getEvaluateIdx());
        if (userAdminService.isAlreadyBlackList(userIdx)) {
            return new HashMap<>() {{
                put("Success", false);
            }};
        }

        userAdminService.banUserByExam(
                userIdx,
                365L,
                evaluatePostBlacklistForm.getBannedReason(),
                evaluatePostBlacklistForm.getJudgement());
        userAdminService.plusRestrictCount(userIdx);

        return new HashMap<>() {{
            put("Success", true);
        }};
    }

    public Map<String, Boolean> executeExamPost(ExamPostBlacklistForm examPostBlacklistForm) {
        Long userIdx = userService.loadExamPostsByIndex(examPostBlacklistForm.getExamIdx()).getUser().getId();
        userAdminService.blacklistOrRestrictAndDeleteExamPost(examPostBlacklistForm.getExamIdx());

        if (userAdminService.isAlreadyBlackList(userIdx)) {
            return new HashMap<>() {{
                put("Success", false);
            }};
        }

        userAdminService.banUserByEvaluate(userIdx, 365L,
                examPostBlacklistForm.getBannedReason(),
                examPostBlacklistForm.getJudgement());
        userAdminService.plusRestrictCount(userIdx);

        userAdminService.banUserByExam(
                userIdx,
                365L,
                examPostBlacklistForm.getBannedReason(),
                examPostBlacklistForm.getJudgement());
        userAdminService.plusRestrictCount(userIdx);

        return new HashMap<>() {{
            put("Success", true);
        }};
    }
}
