package usw.suwiki.domain.user.service;

import usw.suwiki.domain.user.User;
import usw.suwiki.secure.encode.PasswordEncoder;

import java.util.Optional;

public interface UserIsolationCRUDService {
  boolean isIsolatedByEmail(String email);

  boolean isIsolatedByLoginId(String loginId);

  boolean isRetrievedUserEquals(String email, String loginId);

  boolean isLoginableIsolatedUser(String loginId, String inputPassword, PasswordEncoder passwordEncoder);

  Optional<String> getIsolatedLoginIdByEmail(String email);

  String updateIsolatedUserPassword(PasswordEncoder passwordEncoder, String email); // todo: refactoring 할 것

  User awakeIsolated(UserCRUDService userCRUDService, String loginId);
}
