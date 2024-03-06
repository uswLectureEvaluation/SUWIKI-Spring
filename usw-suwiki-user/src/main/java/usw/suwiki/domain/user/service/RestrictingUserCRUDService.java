package usw.suwiki.domain.user.service;

import java.util.List;

public interface RestrictingUserCRUDService {
  List<LoadMyRestrictedReasonResponseForm> loadRestrictedLog(Long userIdx);
}
