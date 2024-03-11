package usw.suwiki.domain.user.service;

import java.util.List;

import static usw.suwiki.domain.user.dto.UserResponseDto.LoadMyRestrictedReasonResponseForm;

public interface RestrictingUserCRUDService {
  List<LoadMyRestrictedReasonResponseForm> loadRestrictedLog(Long userIdx);
}
