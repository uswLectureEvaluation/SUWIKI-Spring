package usw.suwiki.domain.email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.Role;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserDto;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.domain.user.UserRepository;
import usw.suwiki.domain.user.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

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
        if (userService.isExpired(confirmationToken)) {

            //토큰이 만료되었으면 임시 저장한 유저 삭제
            userRepository.deleteUserNotEmailCheck(confirmationToken.getUser().getId());

            //토큰 테이블에서도 삭제
            confirmationTokenService.deleteAllByToken(token);
        }

        //정상적인 토큰이라면 인증 스탬프
        confirmationTokenService.setConfirmedAt(token);
    }

    //권한 해제
    @Transactional
    public void unRestricted(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new AccountException(ErrorType.EMAIL_VALIDATED_ERROR));
        confirmationToken.getUser().setRestricted(false);
    }

    //유저 생성 타임 스탬프
    @Transactional
    public void userSetCreatedAt(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new AccountException(ErrorType.EMAIL_VALIDATED_ERROR));
        confirmationToken.getUser().setCreatedAt(LocalDateTime.now());
    }

    //유저 업데이트 타임 스탬프
    @Transactional
    public void userSetUpdatedAt(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new AccountException(ErrorType.EMAIL_VALIDATED_ERROR));
        confirmationToken.getUser().setUpdatedAt(LocalDateTime.now());
    }

    //Role.USER 권한부여
    @Transactional
    public void userSetRole(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new AccountException(ErrorType.EMAIL_VALIDATED_ERROR));
        confirmationToken.getUser().setRole(Role.USER);
    }

    //사용자 ViewExamCount 셋팅
    @Transactional
    public void userSetViewExamCount(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new AccountException(ErrorType.EMAIL_VALIDATED_ERROR));
        confirmationToken.getUser().setViewExamCount(0);
    }

    @Transactional
    public boolean isUserEmailAuthed(UserDto.LoginForm loginForm) {

        Optional<User> user = userRepository.findByLoginId(loginForm.getLoginId());

        //이메일 인증 토큰에 대한 유저가 없으면 false
        if (user.isEmpty()) { return false; }

        //유저 인덱스 따오기
        Long userIdx = user.get().getId();

        //유저인덱스가 존재하는지 확인
        return confirmationTokenRepository.isUserConfirmed(userIdx).isPresent();
    }
}