package usw.suwiki.domain.restrictinguser.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.restrictinguser.RestrictingUser;
import usw.suwiki.domain.restrictinguser.repository.RestrictingUserRepository;
import usw.suwiki.domain.user.user.controller.dto.UserResponseDto.LoadMyRestrictedReasonResponseForm;

@Service
@RequiredArgsConstructor
@Transactional
public class RestrictingUserCRUDService {

    private final RestrictingUserRepository restrictingUserRepository;


    @Transactional(readOnly = true)
    public List<LoadMyRestrictedReasonResponseForm> loadRestrictedLog(Long userIdx) {
        Optional<RestrictingUser> wrappedRestrictingUser = restrictingUserRepository.findByUserIdx(userIdx);
        List<LoadMyRestrictedReasonResponseForm> finalResultForm = new ArrayList<>();

        if (wrappedRestrictingUser.isPresent()) {
            RestrictingUser RestrictingUser = wrappedRestrictingUser.get();
            LoadMyRestrictedReasonResponseForm resultForm = LoadMyRestrictedReasonResponseForm
                .builder()
                .restrictedReason(RestrictingUser.getRestrictingReason())
                .judgement(RestrictingUser.getJudgement())
                .createdAt(RestrictingUser.getCreatedAt())
                .restrictingDate(RestrictingUser.getRestrictingDate())
                .build();
            finalResultForm.add(resultForm);
        }

        return finalResultForm;
    }
}
