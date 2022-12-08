package usw.suwiki.domain.restrictinguser.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.restrictinguser.repository.RestrictingUser;
import usw.suwiki.domain.user.dto.UserResponseDto.ViewMyRestrictedReasonForm;
import usw.suwiki.domain.user.repository.UserRepository;
import usw.suwiki.domain.user.repository.restrictinguser.RestrictingUserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestrictingUserService {

    private final RestrictingUserRepository restrictingUserRepository;
    private final UserRepository userRepository;

    // 정지내역 내역 모두보기
    @Transactional
    public List<ViewMyRestrictedReasonForm> loadRestrictedLog(Long userIdx) {

        List<RestrictingUser> loadedDomain = restrictingUserRepository.findByUserIdx(userIdx);
        List<ViewMyRestrictedReasonForm> finalResultForm = new ArrayList<>();

        if (loadedDomain.toArray().length > 0) {
            for (RestrictingUser target : loadedDomain) {
                ViewMyRestrictedReasonForm resultForm = new ViewMyRestrictedReasonForm();
                String extractedBannedReason = target.getRestrictingReason();
                String extractedJudgement = target.getJudgement();
                LocalDateTime extractedCreatedAt = target.getCreatedAt();
                LocalDateTime restrictingDate = target.getRestrictingDate();

                resultForm.setRestrictedReason(extractedBannedReason);
                resultForm.setJudgement(extractedJudgement);
                resultForm.setCreatedAt(extractedCreatedAt);
                resultForm.setRestrictingDate(restrictingDate);
                finalResultForm.add(resultForm);
            }
        }
        return finalResultForm;
    }

    // 이용정지를 풀기 위한 메서드 --> 정지 테이블에서 유저 삭제
    @Transactional
    // 초 분 시 일 월 주 년
    @Scheduled(cron = "10 * * * * *")
    public void isUnrestrictedTarget() {
        // 현재시각으로부터 - 30일
        LocalDateTime targetTime = LocalDateTime.now().minusDays(30);
        List<RestrictingUser> targetUser = restrictingUserRepository.findByRestrictingDateBefore(targetTime);
        for (RestrictingUser target : targetUser) {
            Long userIdx = target.getUserIdx();
            restrictingUserRepository.deleteByUserIdx(userIdx);
            userRepository.unRestricted(userIdx);
        }
    }
}
