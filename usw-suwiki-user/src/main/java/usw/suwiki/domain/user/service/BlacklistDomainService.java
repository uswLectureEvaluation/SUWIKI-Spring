package usw.suwiki.domain.user.service;

public interface BlacklistDomainService {
  void isUserInBlackListThatRequestJoin(String email);
}
