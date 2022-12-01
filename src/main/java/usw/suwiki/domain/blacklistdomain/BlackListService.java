package usw.suwiki.domain.blacklistdomain;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.blacklistdomain.entity.BlacklistDomain;
import usw.suwiki.domain.user.repository.UserRepository;
import usw.suwiki.domain.user.dto.UserResponseDto;
import usw.suwiki.exception.errortype.AccountException;
import usw.suwiki.exception.ErrorType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static usw.suwiki.exception.ErrorType.YOU_ARE_IN_BLACKLIST;


@Service
@RequiredArgsConstructor
@Transactional
public class BlackListService {

    private final BlacklistRepository blacklistRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    // 블랙리스트 풀렸는지 확인
    public List<BlacklistDomain> beReleased() {
        LocalDateTime targetTime = LocalDateTime.now();
        return blacklistRepository.findByExpiredAtBefore(targetTime);
    }

    // 블랙리스트 풀렸으면 해줄 것 (Restricted false 로 변환, 블랙리스트에 로우 제거)
    @Scheduled(cron = "0 0 0 * * *")
    public void whiteList() {
        List<BlacklistDomain> whiteListTarget = beReleased();

        for (int i = 0; i < whiteListTarget.toArray().length; i++) {
            Long userIdx = whiteListTarget.get(i).getId();
            userRepository.unRestricted(userIdx);
            blacklistRepository.deleteByUserIdx(userIdx);
        }
    }
    @Transactional
    public void isBlackList(String email) {
        List<BlacklistDomain> blacklist = blacklistRepository.findAllBlacklist();

        for (BlacklistDomain bUser : blacklist) {
            if (bCryptPasswordEncoder.matches(email, bUser.getHashedEmail())) {
                throw new AccountException(YOU_ARE_IN_BLACKLIST);
            }
        }
    }

    // 블랙리스트 내역 모두보기 DTO 로 Typing
    @Transactional
    public List<UserResponseDto.ViewMyBlackListReasonForm> getBlacklistLog(Long userIdx) {
        List<BlacklistDomain> loadedDomain = blacklistRepository.findByUserIdx(userIdx);
        List<UserResponseDto.ViewMyBlackListReasonForm> finalResultForm = new ArrayList<>();

        if (loadedDomain.toArray().length > 0) {
            for (BlacklistDomain target : loadedDomain) {
                UserResponseDto.ViewMyBlackListReasonForm resultForm = new UserResponseDto.ViewMyBlackListReasonForm();
                String extractedBannedReason = target.getBannedReason();
                String extractedJudgement = target.getJudgement();
                LocalDateTime extractedCreatedAt = target.getCreatedAt();
                LocalDateTime extractedExpiredAt = target.getExpiredAt();
                resultForm.setBlackListReason(extractedBannedReason);
                resultForm.setJudgement(extractedJudgement);
                resultForm.setCreatedAt(extractedCreatedAt);
                resultForm.setExpiredAt(extractedExpiredAt);
                finalResultForm.add(resultForm);
            }
        }
        return finalResultForm;
    }
}