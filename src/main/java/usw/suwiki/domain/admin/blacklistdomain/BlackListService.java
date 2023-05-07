package usw.suwiki.domain.admin.blacklistdomain;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.user.dto.UserResponseDto.LoadMyBlackListReasonResponseForm;
import usw.suwiki.domain.user.user.entity.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.global.exception.errortype.AccountException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static usw.suwiki.global.exception.ExceptionType.YOU_ARE_IN_BLACKLIST;


@Service
@RequiredArgsConstructor
@Transactional
public class BlackListService {

    private static final Long BANNED_PERIOD = 365L;
    private static final Integer NESTED_RESTRICTED_TIME = 3;

    private final BlacklistRepository blacklistRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Scheduled(cron = "0 0 0 * * *")
    public void whiteList() {
        List<BlacklistDomain> whiteListTarget = beReleased();
        for (int i = 0; i < whiteListTarget.toArray().length; i++) {
            User user = userRepository.findById(whiteListTarget.get(i).getId()).get();
            user.editRestricted(false);
            blacklistRepository.deleteByUserIdx(user.getId());
        }
    }

    public List<LoadMyBlackListReasonResponseForm> loadBlacklistLog(Long userIdx) {
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

    public void executeBlacklist(
            Long userIdx,
            Long bannedPeriod,
            String bannedReason,
            String judgement
    ) {
        User user = userRepository.findById(userIdx).get();
//        if (isUserAlreadyInBlackListFromUser(user)) {
//
//        }
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

    public void isUserInBlackListThatRequestJoin(String email) {
        List<BlacklistDomain> blacklist = blacklistRepository.findAll();
        for (BlacklistDomain blackListUser : blacklist) {
            if (bCryptPasswordEncoder.matches(email, blackListUser.getHashedEmail())) {
                throw new AccountException(YOU_ARE_IN_BLACKLIST);
            }
        }
    }

    // 블랙리스트 풀렸는지 확인
    private List<BlacklistDomain> beReleased() {
        LocalDateTime targetTime = LocalDateTime.now();
        return blacklistRepository.findByExpiredAtBefore(targetTime);
    }

    private boolean isUserAlreadyInBlackListFromUser(User user) {
        return blacklistRepository.findByUserIdx(user.getId()).isPresent();
    }
}
