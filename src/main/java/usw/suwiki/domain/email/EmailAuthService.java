package usw.suwiki.domain.email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.Role;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.domain.user.UserRepository;
import usw.suwiki.domain.user.UserService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserRepository userRepository;

    //이메일 인증 토큰 검증
    @Transactional
    public void confirmToken(String token) {
        
        //토큰 받아오기
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
                .orElseThrow(() -> new AccountException(ErrorType.EMAIL_VALIDATED_ERROR));
        
        //이미 인증에 사용한 토큰이면 -> 에러
        if (confirmationToken.getConfirmedAt() != null) throw new AccountException(ErrorType.EMAIL_AUTH_TOKEN_ALREADY_USED);

        //토큰이 만료 됐으면
        if (userService.isEmailAuthTokenExpired(confirmationToken)) {

            //토큰이 만료되었으면 임시 저장한 유저 삭제
            userRepository.deleteById(confirmationToken.getUserIdx());

            //토큰 테이블에서도 삭제
            confirmationTokenService.deleteAllByToken(token);
        }

        //정상적인 토큰이라면 인증 스탬프
        confirmationTokenService.setConfirmedAt(token);
    }

    @Transactional
    public void mailAuthSuccess(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
                .orElseThrow(() -> new AccountException(ErrorType.EMAIL_VALIDATED_ERROR));

        Long userIdx = confirmationToken.getUserIdx();

        userService.loadUserFromUserIdx(userIdx).setRestricted(false);
        userService.loadUserFromUserIdx(userIdx).setCreatedAt(LocalDateTime.now());
        userService.loadUserFromUserIdx(userIdx).setUpdatedAt(LocalDateTime.now());
        userService.loadUserFromUserIdx(userIdx).setRole(Role.USER);
    }
}