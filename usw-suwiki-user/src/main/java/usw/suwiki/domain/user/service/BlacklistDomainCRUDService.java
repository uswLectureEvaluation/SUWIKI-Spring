package usw.suwiki.domain.user.service;

import java.util.List;

import static usw.suwiki.domain.user.dto.UserResponseDto.LoadMyBlackListReasonResponseForm;

public interface BlacklistDomainCRUDService {
  List<LoadMyBlackListReasonResponseForm> loadAllBlacklistLog(Long userIdx);

  void saveBlackListDomain(Long userIdx, Long bannedPeriod, String bannedReason, String judgement);
}
