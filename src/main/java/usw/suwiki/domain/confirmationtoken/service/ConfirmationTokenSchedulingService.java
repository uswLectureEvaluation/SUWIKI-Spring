package usw.suwiki.domain.confirmationtoken.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.confirmationtoken.ConfirmationToken;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.service.UserCRUDService;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class ConfirmationTokenSchedulingService {

    private final UserCRUDService userCRUDService;
    private final ConfirmationTokenCRUDService confirmationTokenCRUDService;

    @Transactional
    @Scheduled(cron = "0 0 * * * * ")
    public void isNotConfirmedEmail() {
        log.info("{} - 이메일 인증을 수행하지 않은 유저 검증 시작", LocalDateTime.now());
        List<ConfirmationToken> confirmationTokens = confirmationTokenCRUDService.loadNotConfirmedTokens(
            LocalDateTime.now().minusMinutes(30)
        );
        for (ConfirmationToken confirmationToken : confirmationTokens) {
            User targetUser = userCRUDService.loadUserFromUserIdx(confirmationToken.getUserIdx());
            confirmationTokenCRUDService.deleteFromId(confirmationToken.getId());
            userCRUDService.deleteFromUserIdx(targetUser.getId());
        }
        log.info("{} - 이메일 인증을 수행하지 않은 유저 검증 종료", LocalDateTime.now());
    }
}
