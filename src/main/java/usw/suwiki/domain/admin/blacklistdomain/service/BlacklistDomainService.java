package usw.suwiki.domain.admin.blacklistdomain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.blacklistdomain.BlacklistDomain;
import usw.suwiki.domain.admin.blacklistdomain.repository.BlacklistRepository;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.global.exception.errortype.AccountException;

import java.time.LocalDateTime;
import java.util.List;

import static usw.suwiki.global.exception.ExceptionType.YOU_ARE_IN_BLACKLIST;


@Service
@RequiredArgsConstructor
@Transactional
public class BlacklistDomainService {

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
}
