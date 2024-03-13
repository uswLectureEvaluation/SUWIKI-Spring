package usw.suwiki.core.mail;

import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.util.Collections.emptyList;

@RequiredArgsConstructor
public enum MailType {
  FIND_ID(List.of("loginId")),
  EMAIL_AUTH(List.of("redirectUrl")),
  FIND_PASSWORD(List.of("password")),

  ERROR(emptyList()),
  USED_LINK(emptyList()),
  EXPIRED_LINK(emptyList()),
  AUTH_SUCCESS(emptyList()),
  DELETE_WARNING(emptyList()),
  DORMANT_NOTIFICATION(emptyList()),
  PRIVACY_POLICY_NOTIFICATION(emptyList()),
  ;

  private final List<String> parameters;

  public String template() {
    return this.name().replace("_", "-");
  }

  public String parameter() {
    if (parameters.isEmpty()) {
      throw new IllegalArgumentException("no parameter required!!");
    }

    return parameters.get(0);
  }

  public boolean isEmailAuth() {
    return this.equals(EMAIL_AUTH);
  }
}
