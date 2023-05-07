package usw.suwiki.domain.admin.restrictinguser;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.admin.dto.UserAdminRequestDto.EvaluatePostRestrictForm;
import usw.suwiki.domain.admin.admin.dto.UserAdminRequestDto.ExamPostRestrictForm;
import usw.suwiki.domain.admin.admin.service.UserAdminService;
import usw.suwiki.domain.admin.blacklistdomain.BlackListService;
import usw.suwiki.domain.evaluation.entity.EvaluatePosts;
import usw.suwiki.domain.evaluation.service.EvaluatePostsService;
import usw.suwiki.domain.exam.domain.ExamPosts;
import usw.suwiki.domain.exam.service.ExamPostsService;
import usw.suwiki.domain.user.user.dto.UserResponseDto.LoadMyRestrictedReasonResponseForm;
import usw.suwiki.domain.user.user.entity.User;
import usw.suwiki.domain.user.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RestrictingUserService {

    private final static String BANNED_REASON = "신고 누적으로 인한 블랙리스트";
    private final static String JUDGEMENT = "신고누적 블랙리스트 1년";
    private final static Long BANNED_PERIOD = 90L;

    private final UserService userService;
    private final EvaluatePostsService evaluatePostsService;
    private final ExamPostsService examPostsService;
    private final UserAdminService userAdminService;
    private final BlackListService blackListService;
    private final RestrictingUserRepository restrictingUserRepository;

    public void executeRestrictUserFromEvaluatePost(
            EvaluatePostRestrictForm evaluatePostRestrictForm
    ) {
        EvaluatePosts evaluatePost = evaluatePostsService
                .loadEvaluatePostsFromEvaluatePostsIdx(evaluatePostRestrictForm.getEvaluateIdx());
        User user = userService.loadUserFromUserIdx(evaluatePost.getUser().getId());

        if (user.getRestrictedCount() >= 2) {
            blackListService.executeBlacklist(
                    user.getId(),
                    BANNED_PERIOD,
                    BANNED_REASON,
                    JUDGEMENT
            );
        } else if (user.getRestrictedCount() < 3) {
            user.editRestricted(true);
            restrictingUserRepository.save(
                    RestrictingUser.builder()
                            .userIdx(user.getId())
                            .restrictingDate(LocalDateTime.now().plusDays(evaluatePostRestrictForm.getRestrictingDate()))
                            .restrictingReason(evaluatePostRestrictForm.getRestrictingReason())
                            .judgement(evaluatePostRestrictForm.getJudgement())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build()
            );
        }
    }

    public void executeRestrictUserFromExamPost(ExamPostRestrictForm examPostRestrictForm) {
        ExamPosts examPost = examPostsService
                .loadExamPostsFromExamPostsIdx(examPostRestrictForm.getExamIdx());
        User user = userService.loadUserFromUserIdx(examPost.getUser().getId());

        if (user.getRestrictedCount() >= 2) {
            blackListService.executeBlacklist(
                    user.getId(),
                    BANNED_PERIOD,
                    BANNED_REASON,
                    JUDGEMENT
            );
        } else if (user.getRestrictedCount() < 3) {
            user.editRestricted(true);
            restrictingUserRepository.save(
                    RestrictingUser.builder()
                            .userIdx(user.getId())
                            .restrictingDate(LocalDateTime.now().plusDays(examPostRestrictForm.getRestrictingDate()))
                            .restrictingReason(examPostRestrictForm.getRestrictingReason())
                            .judgement(examPostRestrictForm.getJudgement())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now()).build()
            );
        }
    }

    @Transactional(readOnly = true)
    public List<LoadMyRestrictedReasonResponseForm> loadRestrictedLog(Long userIdx) {
        Optional<RestrictingUser> wrappedRestrictingUser = restrictingUserRepository.findByUserIdx(userIdx);
        List<LoadMyRestrictedReasonResponseForm> finalResultForm = new ArrayList<>();

        if (wrappedRestrictingUser.isPresent()) {
            RestrictingUser RestrictingUser = wrappedRestrictingUser.get();
            LoadMyRestrictedReasonResponseForm resultForm = LoadMyRestrictedReasonResponseForm
                    .builder()
                    .restrictedReason(RestrictingUser.getRestrictingReason())
                    .judgement(RestrictingUser.getJudgement())
                    .createdAt(RestrictingUser.getCreatedAt())
                    .restrictingDate(RestrictingUser.getRestrictingDate())
                    .build();
            finalResultForm.add(resultForm);
        }

        return finalResultForm;
    }

    @Scheduled(cron = "10 0 0 * * *")
    public void isUnrestrictedTarget() {
        List<RestrictingUser> restrictingUsers =
                restrictingUserRepository.findByRestrictingDateBefore(LocalDateTime.now());
        for (RestrictingUser restrictingUser : restrictingUsers) {
            User user = userService.loadUserFromUserIdx(restrictingUser.getUserIdx());
            user.editRestricted(false);
            restrictingUserRepository.deleteByUserIdx(user.getId());
        }
    }

    public void deleteFromUserIdx(Long userIdx) {
        restrictingUserRepository.deleteByUserIdx(userIdx);
    }
}
