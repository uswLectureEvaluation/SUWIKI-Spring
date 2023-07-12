package usw.suwiki.domain.restrictinguser.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.controller.dto.UserAdminRequestDto.EvaluatePostRestrictForm;
import usw.suwiki.domain.admin.controller.dto.UserAdminRequestDto.ExamPostRestrictForm;
import usw.suwiki.domain.blacklistdomain.service.BlacklistDomainCRUDService;
import usw.suwiki.domain.evaluation.service.EvaluatePostCRUDService;
import usw.suwiki.domain.exam.domain.ExamPosts;
import usw.suwiki.domain.exam.service.ExamPostCRUDService;
import usw.suwiki.domain.restrictinguser.RestrictingUser;
import usw.suwiki.domain.restrictinguser.repository.RestrictingUserRepository;
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
    private final EvaluatePostCRUDService evaluatePostCRUDService;
    private final ExamPostCRUDService examPostCRUDService;
    private final BlacklistDomainCRUDService blacklistDomainCRUDService;
    private final RestrictingUserRepository restrictingUserRepository;

    public void executeRestrictUserFromEvaluatePost(
            EvaluatePostRestrictForm evaluatePostRestrictForm,
            Long reportedUserId
    ) {
        User user = userCRUDService.loadUserFromUserIdx(reportedUserId);

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
                            .restrictingDate(LocalDateTime.now().plusDays(evaluatePostRestrictForm.restrictingDate()))
                            .restrictingReason(evaluatePostRestrictForm.restrictingReason())
                            .judgement(evaluatePostRestrictForm.judgement())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build()
            );
        }
    }

    public void executeRestrictUserFromExamPost(
            ExamPostRestrictForm examPostRestrictForm,
            Long reportedUserId
    ) {
        User user = userCRUDService.loadUserFromUserIdx(reportedUserId);

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
                            .restrictingDate(LocalDateTime.now().plusDays(examPostRestrictForm.restrictingDate()))
                            .restrictingReason(examPostRestrictForm.restrictingReason())
                            .judgement(examPostRestrictForm.judgement())
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
