package usw.suwiki.domain.restrictinguser.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.controller.dto.UserAdminRequestDto.EvaluatePostRestrictForm;
import usw.suwiki.domain.admin.controller.dto.UserAdminRequestDto.ExamPostRestrictForm;
import usw.suwiki.domain.blacklistdomain.service.BlacklistDomainCRUDService;
import usw.suwiki.domain.restrictinguser.RestrictingUser;
import usw.suwiki.domain.restrictinguser.repository.RestrictingUserRepository;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.service.UserCRUDService;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RestrictingUserService {

    private final static String BANNED_REASON = "신고 누적으로 인한 블랙리스트";
    private final static String JUDGEMENT = "신고누적 블랙리스트 1년";
    private final static Long BANNED_PERIOD = 90L;

    private final UserCRUDService userCRUDService;
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

    public void deleteFromUserIdx(Long userIdx) {
        restrictingUserRepository.deleteByUserIdx(userIdx);
    }
}
