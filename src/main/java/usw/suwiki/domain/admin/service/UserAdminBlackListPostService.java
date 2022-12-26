package usw.suwiki.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.EvaluatePostBlacklistForm;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.ExamPostBlacklistForm;
import usw.suwiki.domain.admin.service.UserAdminCommonService;
import usw.suwiki.domain.user.service.UserCommonService;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAdminBlackListPostService {

    private final UserCommonService userCommonService;
    private final UserAdminCommonService userAdminCommonService;

    public Map<String, Boolean> executeEvaluatePost(EvaluatePostBlacklistForm evaluatePostBlacklistForm) {
        Long userIdx = userCommonService.loadEvaluatePostsByIndex(evaluatePostBlacklistForm.getEvaluateIdx()).getUser().getId();
        userAdminCommonService.banishEvaluatePost(evaluatePostBlacklistForm.getEvaluateIdx());
        if (userAdminCommonService.isAlreadyBlackList(userIdx)) {
            return new HashMap<>() {{
                put("Success", false);
            }};
        }

        userAdminCommonService.executeBlacklistByEvaluatePost(userIdx, 365L,
                evaluatePostBlacklistForm.getBannedReason(),
                evaluatePostBlacklistForm.getJudgement());
        userAdminCommonService.plusRestrictCount(userIdx);

        return new HashMap<>() {{
            put("Success", true);
        }};
    }

    public Map<String, Boolean> executeExamPost(ExamPostBlacklistForm examPostBlacklistForm) {
        Long userIdx = userCommonService.loadExamPostsByIndex(examPostBlacklistForm.getExamIdx()).getUser().getId();
        userAdminCommonService.blacklistOrRestrictAndDeleteExamPost(examPostBlacklistForm.getExamIdx());

        if (userAdminCommonService.isAlreadyBlackList(userIdx)) {
            return new HashMap<>() {{
                put("Success", false);
            }};
        }

        userAdminCommonService.executeBlacklistByExamPost(userIdx, 365L,
                examPostBlacklistForm.getBannedReason(),
                examPostBlacklistForm.getJudgement());
        userAdminCommonService.plusRestrictCount(userIdx);

        return new HashMap<>() {{
            put("Success", true);
        }};
    }
}
