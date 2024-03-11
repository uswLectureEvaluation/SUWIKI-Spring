package usw.suwiki.core.secure.model;

// todo : 의존성 분리 시 삭제
public interface Claim {
  String loginId();

  String role();

  boolean restricted();
}
