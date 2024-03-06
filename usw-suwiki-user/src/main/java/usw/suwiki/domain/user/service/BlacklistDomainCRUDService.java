package usw.suwiki.domain.user.service;

import java.util.List;

public interface BlacklistDomainCRUDService {
  List<LoadMyBlackListReasonResponseForm> loadAllBlacklistLog(Long userIdx);

  void saveBlackListDomain(Long userIdx, Long bannedPeriod, String bannedReason, String judgement);
}
