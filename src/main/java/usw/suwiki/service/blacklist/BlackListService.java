package usw.suwiki.service.blacklist;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.blacklistDomain.BlacklistDomain;
import usw.suwiki.repository.blacklist.BlacklistRepository;
import usw.suwiki.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;


//매일 한번 씩 실행하는 스케쥴러 서비스
@Service
@RequiredArgsConstructor
@Transactional
public class BlackListService {

    private final BlacklistRepository blacklistRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    
    //정지 풀렸는지 확인
    @Transactional
    public List<BlacklistDomain> beReleased() {
        LocalDateTime targetTime = LocalDateTime.now();
        return blacklistRepository.findByExpiredAtBefore(targetTime);
    }

    //정지 풀렸으면 해줄 것 (Restricted false 로 변환, 블랙리스트에 로우 제거)
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void whiteList() {
        List<BlacklistDomain> whiteListTarget = beReleased();

        for (int i = 0; i < whiteListTarget.toArray().length; i++) {
            Long userIdx = whiteListTarget.get(i).getId();

            userRepository.unRestricted(userIdx);
            blacklistRepository.deleteByUserId(userIdx);
        }
    }
    
    //블랙리스트 이메일인지 확인, 블랙리스트에 없으면 true
    @Transactional
    public boolean isBlackList(String email) {
        return blacklistRepository.findByHashedEmail(bCryptPasswordEncoder.encode(email)).isPresent();
    }
}
