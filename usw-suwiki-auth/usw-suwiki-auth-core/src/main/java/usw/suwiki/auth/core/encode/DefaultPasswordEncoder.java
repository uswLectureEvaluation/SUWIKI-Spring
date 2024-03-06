package usw.suwiki.auth.core.encode;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import usw.suwiki.core.secure.PasswordEncoder;

@Component
@RequiredArgsConstructor
class DefaultPasswordEncoder implements PasswordEncoder {
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public String encode(String rawPassword) {
    return bCryptPasswordEncoder.encode(rawPassword);
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
  }
}
