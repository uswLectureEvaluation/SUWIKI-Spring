package usw.suwiki.domain.blacklistDomain;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.UserRepository;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;

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
        
        //정지 풀렸는지 확인하는 로직 호출
        List<BlacklistDomain> whiteListTarget = beReleased();

        for (int i = 0; i < whiteListTarget.toArray().length; i++) {
            Long userIdx = whiteListTarget.get(i).getId();

            //권한 해제
            userRepository.unRestricted(userIdx);

            //블랙리스트 테이블에서 제거
            blacklistRepository.deleteByUserId(userIdx);
        }
    }
    
    //블랙리스트 이메일인지 확인, 블랙리스트에 있으면 true
    @Transactional
    public void isBlackList(String email) {

        blacklistRepository.findByHashedEmail(bCryptPasswordEncoder.encode(email))
                .orElseThrow(() -> new AccountException(ErrorType.YOU_ARE_IN_BLACKLIST));
    }
}
