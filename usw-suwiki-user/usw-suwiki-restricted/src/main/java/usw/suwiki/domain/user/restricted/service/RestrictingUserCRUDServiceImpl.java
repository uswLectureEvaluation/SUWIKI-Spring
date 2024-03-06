package usw.suwiki.domain.user.restricted.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.restricted.RestrictingUser;
import usw.suwiki.domain.user.restricted.RestrictingUserRepository;
import usw.suwiki.domain.user.service.RestrictingUserCRUDService;
import usw.suwiki.domain.user.user.controller.dto.UserResponseDto.LoadMyRestrictedReasonResponseForm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class RestrictingUserCRUDServiceImpl implements RestrictingUserCRUDService {

    private final RestrictingUserRepository restrictingUserRepository;

    @Override
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
