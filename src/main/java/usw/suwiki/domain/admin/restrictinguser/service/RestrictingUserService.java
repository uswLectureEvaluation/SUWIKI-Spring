package usw.suwiki.domain.admin.restrictinguser.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.admin.dto.UserAdminRequestDto.EvaluatePostRestrictForm;
import usw.suwiki.domain.admin.admin.dto.UserAdminRequestDto.ExamPostRestrictForm;
import usw.suwiki.domain.admin.blacklistdomain.service.BlacklistDomainCRUDService;
import usw.suwiki.domain.admin.restrictinguser.RestrictingUser;
import usw.suwiki.domain.admin.restrictinguser.repository.RestrictingUserRepository;
import usw.suwiki.domain.evaluation.entity.EvaluatePosts;
import usw.suwiki.domain.evaluation.service.EvaluatePostsService;
import usw.suwiki.domain.exam.domain.ExamPosts;
import usw.suwiki.domain.exam.service.ExamPostsService;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.service.UserCRUDService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RestrictingUserService {

    private final static String BANNED_REASON = "신고 누적으로 인한 블랙리스트";
    private final static String JUDGEMENT = "신고누적 블랙리스트 1년";
    private final static Long BANNED_PERIOD = 90L;

    private final UserCRUDService userCRUDService;
    private final EvaluatePostsService evaluatePostsService;
    private final ExamPostsService examPostsService;
    private final BlacklistDomainCRUDService blacklistDomainCRUDService;
    private final RestrictingUserRepository restrictingUserRepository;

    public void executeRestrictUserFromEvaluatePost(
            EvaluatePostRestrictForm evaluatePostRestrictForm
    ) {
        EvaluatePosts evaluatePost = evaluatePostsService
                .loadEvaluatePostsFromEvaluatePostsIdx(evaluatePostRestrictForm.getEvaluateIdx());
        User user = userCRUDService.loadUserFromUserIdx(evaluatePost.getUser().getId());

        if (user.getRestrictedCount() >= 2) {
            blacklistDomainCRUDService.saveBlackListDomain(
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
        User user = userCRUDService.loadUserFromUserIdx(examPost.getUser().getId());

        if (user.getRestrictedCount() >= 2) {
            blacklistDomainCRUDService.saveBlackListDomain(
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


    @Scheduled(cron = "10 0 0 * * *")
    public void isUnrestrictedTarget() {
        List<RestrictingUser> restrictingUsers =
                restrictingUserRepository.findByRestrictingDateBefore(LocalDateTime.now());
        for (RestrictingUser restrictingUser : restrictingUsers) {
            User user = userCRUDService.loadUserFromUserIdx(restrictingUser.getUserIdx());
            user.editRestricted(false);
            restrictingUserRepository.deleteByUserIdx(user.getId());
        }
    }

    public void deleteFromUserIdx(Long userIdx) {
        restrictingUserRepository.deleteByUserIdx(userIdx);
    }
}
