package usw.suwiki.core.secure;

public interface PasswordEncoder {
  String encode(String rawPassword);

  boolean matches(CharSequence rawPassword, String encodedPassword);
}
