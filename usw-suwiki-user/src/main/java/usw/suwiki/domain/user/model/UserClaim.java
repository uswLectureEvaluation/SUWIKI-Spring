package usw.suwiki.domain.user.model;

import usw.suwiki.core.secure.model.Claim;

// todo : 의존성 분리 시 삭제
public record UserClaim(
  String loginId,
  String role,
  boolean restricted
) implements Claim {
}
