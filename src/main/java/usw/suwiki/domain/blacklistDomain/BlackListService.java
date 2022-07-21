package usw.suwiki.domain.blacklistDomain;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.UserRepository;
import usw.suwiki.domain.user.UserResponseDto;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class BlackListService {

    private final BlacklistRepository blacklistRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    // 블랙리스트 풀렸는지 확인
    @Transactional
    public List<BlacklistDomain> beReleased() {
        LocalDateTime targetTime = LocalDateTime.now();
        return blacklistRepository.findByExpiredAtBefore(targetTime);
    }

    // 블랙리스트 풀렸으면 해줄 것 (Restricted false 로 변환, 블랙리스트에 로우 제거)
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
            blacklistRepository.deleteByUserIdx(userIdx);
        }
    }
    
    // 블랙리스트 이메일인지 확인, 블랙리스트에 있으면 true
    @Transactional
    public void isBlackList(String email) {
        if (blacklistRepository.findByHashedEmail(bCryptPasswordEncoder.encode(email)).isPresent())
            throw new AccountException(ErrorType.YOU_ARE_IN_BLACKLIST);
    }

    // 블랙리스트 내역 모두보기 DTO 로 Typing
    @Transactional
    public List<UserResponseDto.ViewMyBlackListReasonForm> getBlacklistLog(Long userIdx) {

        List<BlacklistDomain> loadedDomain = blacklistRepository.findByUserIdx(userIdx);
        
        List<UserResponseDto.ViewMyBlackListReasonForm> finalResultForm = new ArrayList<>();


        // 블랙리스트 내역 조회하기
        if (loadedDomain.toArray().length > 0) {

            for (BlacklistDomain target : loadedDomain) {

                UserResponseDto.ViewMyBlackListReasonForm resultForm = new UserResponseDto.ViewMyBlackListReasonForm();

                String extractedBannedReason = target.getBannedReason();
                String extractedJudgement = target.getJudgement();
                LocalDateTime extractedCreatedAt = target.getCreatedAt();
                LocalDateTime extractedExpiredAt = target.getExpiredAt();

                // 블랙리스트 사유
                resultForm.setBlackListReason(extractedBannedReason);

                // 조치사항
                resultForm.setJudgement(extractedJudgement);

                // 블랙리스트 추가 일시
                resultForm.setCreatedAt(extractedCreatedAt);

                // 블랙리스트 해제 되는 시각
                resultForm.setExpiredAt(extractedExpiredAt);

                finalResultForm.add(resultForm);
            }

        }
        return finalResultForm;
    }
}
