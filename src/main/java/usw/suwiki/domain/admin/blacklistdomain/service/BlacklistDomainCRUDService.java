package usw.suwiki.domain.admin.blacklistdomain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.blacklistdomain.BlacklistDomain;
import usw.suwiki.domain.admin.blacklistdomain.repository.BlacklistRepository;
import usw.suwiki.domain.user.user.controller.dto.UserResponseDto.LoadMyBlackListReasonResponseForm;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.service.UserCRUDService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BlacklistDomainCRUDService {
    private static final Long BANNED_PERIOD = 365L;
    private static final Integer NESTED_RESTRICTED_TIME = 3;

    private final BlacklistRepository blacklistRepository;
    private final UserCRUDService userCRUDService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public List<LoadMyBlackListReasonResponseForm> loadAllBlacklistLog(Long userIdx) {
        Optional<BlacklistDomain> loadedDomain = blacklistRepository.findByUserIdx(userIdx);
        List<LoadMyBlackListReasonResponseForm> finalResultForm = new ArrayList<>();
        if (loadedDomain.isPresent()) {
            LoadMyBlackListReasonResponseForm loadMyBlackListReasonResponseForm =
                    LoadMyBlackListReasonResponseForm.builder()
                            .blackListReason(loadedDomain.get().getBannedReason())
                            .judgement(loadedDomain.get().getJudgement())
                            .createdAt(loadedDomain.get().getCreatedAt())
                            .expiredAt(loadedDomain.get().getExpiredAt())
                            .build();
            finalResultForm.add(loadMyBlackListReasonResponseForm);
        }
        return finalResultForm;
    }

    public void saveBlackListDomain(
            Long userIdx,
            Long bannedPeriod,
            String bannedReason,
            String judgement
    ) {
        User user = userCRUDService.loadUserFromUserIdx(userIdx);
        user.editRestricted(true);
        String hashTargetEmail = bCryptPasswordEncoder.encode(user.getEmail());
        if (user.getRestrictedCount() >= NESTED_RESTRICTED_TIME) {
            bannedPeriod += BANNED_PERIOD;
        }

        BlacklistDomain blacklistDomain = BlacklistDomain.builder()
                .userIdx(user.getId())
                .bannedReason(bannedReason)
                .hashedEmail(hashTargetEmail)
                .judgement(judgement)
                .expiredAt(LocalDateTime.now().plusDays(bannedPeriod))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        blacklistRepository.save(blacklistDomain);
    }
}
