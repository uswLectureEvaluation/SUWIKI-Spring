package usw.suwiki.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.auth.token.service.ConfirmUserService;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.domain.user.UserRepository;

import static usw.suwiki.core.exception.ExceptionType.USER_NOT_FOUND;

// todo: 토큰 모듈 의존성 역전용 서비스 (삭제 예정)
@Service
@Transactional
@RequiredArgsConstructor
public class ConfirmUserServiceImpl implements ConfirmUserService {
  private final UserRepository userRepository;

  @Override
  public void delete(Long userId) {
    userRepository.deleteById(userId);
  }

  @Override
  public void activated(Long userId) {
    userRepository.findById(userId)
      .orElseThrow(() -> new AccountException(USER_NOT_FOUND))
      .activateUser();
  }
}
