package usw.suwiki.domain.user.restrictingUser;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.UserResponseDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestrictingUserService {

    private final RestrictingUserRepository restrictingUserRepository;

    // 정지내역 내역 모두보기 DTO 로 Typing
    @Transactional
    public List<UserResponseDto.ViewMyRestrictedReasonForm> getRestrictedLog(Long userIdx) {

        List<RestrictingUser> loadedDomain = restrictingUserRepository.findByUserIdx(userIdx);

        List<UserResponseDto.ViewMyRestrictedReasonForm> finalResultForm = new ArrayList<>();


        // 블랙리스트 내역 조회하기
        if (loadedDomain.toArray().length > 0) {

            for (RestrictingUser target : loadedDomain) {

                UserResponseDto.ViewMyRestrictedReasonForm resultForm = new UserResponseDto.ViewMyRestrictedReasonForm();

                String extractedBannedReason = target.getRestrictingReason();
                String extractedJudgement = target.getJudgement();
                LocalDateTime extractedCreatedAt = target.getCreatedAt();
                LocalDateTime restrictingDate = target.getRestrictingDate();

                // 정지사유
                resultForm.setRestrictedReason(extractedBannedReason);

                // 조치사항
                resultForm.setJudgement(extractedJudgement);

                // 정지 먹은 날짜
                resultForm.setCreatedAt(extractedCreatedAt);

                // 정지 해제 되는 시각
                resultForm.setRestrictingDate(restrictingDate);

                finalResultForm.add(resultForm);
            }

        }


        return finalResultForm;
    }
}
