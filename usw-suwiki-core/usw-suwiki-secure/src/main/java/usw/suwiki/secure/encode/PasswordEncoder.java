package usw.suwiki.secure.encode;

public interface PasswordEncoder {
  String encode(String rawPassword);

  boolean matches(CharSequence rawPassword, String encodedPassword);
}
