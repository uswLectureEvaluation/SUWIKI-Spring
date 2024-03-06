package usw.suwiki.schedule.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.restricted.RestrictingUser;
import usw.suwiki.domain.user.restricted.RestrictingUserRepository;
import usw.suwiki.domain.user.service.UserCRUDService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RestrictingUserSchedulingService {
    private final UserCRUDService userCRUDService;
    private final RestrictingUserRepository restrictingUserRepository;

    @Scheduled(cron = "0 0 * * * *")
    public void isUnrestrictedTarget() {
        log.info("{} - 정지 유저 출소 시작", LocalDateTime.now());
        List<RestrictingUser> restrictingUsers =
            restrictingUserRepository.findByRestrictingDateBefore(LocalDateTime.now());

        for (RestrictingUser restrictingUser : restrictingUsers) {
            User user = userCRUDService.loadUserFromUserIdx(restrictingUser.getUserIdx());
            user.editRestricted(false);
            restrictingUserRepository.deleteByUserIdx(user.getId());
        }

        log.info("{} - 정지 유저 출소 종료", LocalDateTime.now());
    }

    public void deleteFromUserIdx(Long userIdx) {
        restrictingUserRepository.deleteByUserIdx(userIdx);
    }
}
