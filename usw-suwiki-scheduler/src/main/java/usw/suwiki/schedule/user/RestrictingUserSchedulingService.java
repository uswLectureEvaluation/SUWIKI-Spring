package usw.suwiki.schedule.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.service.RestrictingUserService;
import usw.suwiki.domain.user.service.UserCRUDService;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RestrictingUserSchedulingService {
  private final UserCRUDService userCRUDService;
  private final RestrictingUserService restrictingUserService;

  @Scheduled(cron = "0 0 * * * *")
  public void isUnrestrictedTarget() {
    log.info("{} - 정지 유저 출소 시작", LocalDateTime.now());
    for (Long restrictedId : restrictingUserService.loadAllRestrictedUntilNow()) {
      User user = userCRUDService.loadUserFromUserIdx(restrictedId);
      user.released();
      restrictingUserService.releaseByUserId(restrictedId);
    }

    log.info("{} - 정지 유저 출소 종료", LocalDateTime.now());
  }
}
