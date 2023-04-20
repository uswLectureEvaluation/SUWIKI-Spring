package usw.suwiki.domain.user.restrictinguser.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.restrictinguser.repository.RestrictingUser;
import usw.suwiki.domain.user.user.dto.UserResponseDto.LoadMyRestrictedReasonForm;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.domain.user.user.repository.RestrictingUserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestrictingUserCommonService {

    private final RestrictingUserRepository restrictingUserRepository;
    private final UserRepository userRepository;

    // 정지내역 내역 모두보기
    @Transactional
    public List<LoadMyRestrictedReasonForm> loadRestrictedLog(Long userIdx) {

        List<RestrictingUser> loadedDomain = restrictingUserRepository.findByUserIdx(userIdx);
        List<LoadMyRestrictedReasonForm> finalResultForm = new ArrayList<>();

        if (loadedDomain.toArray().length > 0) {
            for (RestrictingUser target : loadedDomain) {
                String extractedRestrictedReason = target.getRestrictingReason();
                String extractedJudgement = target.getJudgement();
                LocalDateTime extractedCreatedAt = target.getCreatedAt();
                LocalDateTime extractedRestrictingDate = target.getRestrictingDate();
                LoadMyRestrictedReasonForm resultForm = LoadMyRestrictedReasonForm.builder()
                        .restrictedReason(extractedRestrictedReason)
                        .judgement(extractedJudgement)
                        .createdAt(extractedCreatedAt)
                        .restrictingDate(extractedRestrictingDate)
                        .build();
                finalResultForm.add(resultForm);
            }
        }
        return finalResultForm;
    }

    // 이용정지를 풀기 위한 메서드 --> 정지 테이블에서 유저 삭제
    @Transactional
    // 초 분 시 일 월 주 년
    @Scheduled(cron = "10 0 0 * * *")
    public void isUnrestrictedTarget() {
        List<RestrictingUser> targetUser = restrictingUserRepository.findByRestrictingDateBefore(LocalDateTime.now());
        for (RestrictingUser target : targetUser) {
            Long userIdx = target.getUserIdx();
            restrictingUserRepository.deleteByUserIdx(userIdx);
            userRepository.updateRestricted(userIdx, false);
        }
    }
}
