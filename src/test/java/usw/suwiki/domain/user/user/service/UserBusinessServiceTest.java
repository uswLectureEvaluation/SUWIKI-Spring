// package usw.suwiki.domain.user.service;
//
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import usw.suwiki.domain.confirmationtoken.ConfirmationToken;
// import usw.suwiki.domain.confirmationtoken.repository.ConfirmationTokenRepository;
// import usw.suwiki.domain.user.user.User;
// import usw.suwiki.domain.user.user.controller.dto.UserRequestDto.*;
// import usw.suwiki.domain.user.user.repository.UserRepository;
// import usw.suwiki.domain.user.user.service.UserBusinessService;
// import usw.suwiki.domain.user.userIsolation.UserIsolation;
// import usw.suwiki.domain.user.userIsolation.repository.UserIsolationRepository;
//
// import java.time.LocalDateTime;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.Objects;
// import java.util.Optional;
//
// import static org.assertj.core.api.Assertions.assertThat;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.Mockito.when;
// import static usw.suwiki.global.exception.ExceptionType.*;
// import static usw.suwiki.global.util.apiresponse.ApiResponseFactory.*;
//
// @ExtendWith(MockitoExtension.class)
// public class UserBusinessServiceTest {
//
//     @Mock
//     BCryptPasswordEncoder bCryptPasswordEncoder;
//     @Mock
//     UserRepository userRepository;
//     @Mock
//     UserIsolationRepository userIsolationRepository;
//     @Mock
//     ConfirmationTokenRepository confirmationTokenRepository;
//     @InjectMocks
//     UserBusinessService userBusinessService;
//
//     @DisplayName("아이디 중복 확인 테스트 - 중복일 시")
//     @Test
//     public void 아이디_중복_확인_테스트_중복일_시() {
//         // Given
//         final String inputLoginId = "diger";
//         final CheckLoginIdForm checkLoginIdForm = new CheckLoginIdForm(inputLoginId);
//         final User user = User.builder().loginId(inputLoginId).build();
//
//         // When
//         when(userRepository.findByLoginId(checkLoginIdForm.getLoginId()))
//                 .thenReturn(Optional.ofNullable(user));
//         Map<String, Boolean> result = userBusinessService.executeCheckId(inputLoginId);
//
//         // Then
//         assertThat(result).isEqualTo(overlapTrueFlag());
//     }
//
//     @DisplayName("아이디 중복 확인 테스트 - 중복이 아닐 시")
//     @Test
//     public void 아이디_중복_확인_테스트_중복이_아닐_시() {
//         // Given
//         final String inputLoginId = "diger";
//         final CheckLoginIdForm checkLoginIdForm = new CheckLoginIdForm(inputLoginId);
//
//         // When
//         when(userRepository.findByLoginId(checkLoginIdForm.getLoginId()))
//                 .thenReturn(Optional.empty());
//         Map<String, Boolean> result = userBusinessService.executeCheckId(inputLoginId);
//
//         // Then
//         assertThat(result).isEqualTo(overlapFalseFlag());
//     }
//
//     @DisplayName("이메일 중복 확인 테스트 - 중복일 시")
//     @Test
//     public void 이메일_중복_확인_테스트_중복일_시() {
//         // Given
//         final String inputEmail = "18018008@suwon.ac.kr";
//         final CheckEmailForm checkEmailForm = new CheckEmailForm(inputEmail);
//         final User user = User.builder().email(inputEmail).build();
//
//         // When
//         when(userRepository.findByEmail(checkEmailForm.getEmail()))
//                 .thenReturn(Optional.ofNullable(user));
//         Map<String, Boolean> result = userBusinessService.executeCheckEmail(inputEmail);
//
//         // Then
//         assertThat(result).isEqualTo(overlapTrueFlag());
//     }
//
//     @DisplayName("이메일 중복 확인 테스트 - 중복이 아닐 시")
//     @Test
//     public void 이메일_중복_확인_테스트_중복이_아닐_시() {
//         // Given
//         final String inputEmail = "18018008@suwon.ac.kr";
//         final CheckEmailForm checkEmailForm = new CheckEmailForm(inputEmail);
//
//         // When
//         when(userRepository.findByEmail(checkEmailForm.getEmail()))
//                 .thenReturn(Optional.empty());
//         Map<String, Boolean> result = userBusinessService.executeCheckEmail(inputEmail);
//
//         // Then
//         assertThat(result).isEqualTo(overlapFalseFlag());
//     }
//
//     @DisplayName("회원 가입 테스트 - 학교 이메일 형식과 다를 경우")
//     @Test
//     void 회원_가입_테스트_학교_이메일_형식과_다를_경우() {
//         // Given
//         final String inputEmail = "18018008@gmail.com";
//         final String inputLoginId = "diger";
//         final String inputPassword = "qwer1234!";
//         final JoinForm joinForm = new JoinForm(inputLoginId, inputPassword, inputEmail);
//
//         // When
//         Throwable exception = assertThrows(RuntimeException.class, () -> {
//             userBusinessService.executeJoin(
//                     joinForm.getLoginId(),
//                     joinForm.getPassword(),
//                     joinForm.getEmail());
//         });
//
//         // Then
//         assertEquals(IS_NOT_EMAIL_FORM.getMessage(), exception.getMessage());
//     }
//
//     @DisplayName("회원 가입 테스트 - 아이디 중복일 시(일반 유저 테이블)")
//     @Test
//     void 회원_가입_테스트_아이디_중복일_시() {
//         // Given
//         final String inputEmail = "18018008@gmail.com";
//         final String inputLoginId = "diger";
//         final String inputPassword = "qwer1234!";
//         final JoinForm joinForm = new JoinForm(inputLoginId, inputPassword, inputEmail);
//
//         // When
//         when(userRepository.findByLoginId(joinForm.getLoginId()))
//                 .thenReturn(Optional.of(new User()));
//
//         Throwable exception = assertThrows(RuntimeException.class, () -> {
//             userBusinessService.executeJoin(
//                     joinForm.getLoginId(),
//                     joinForm.getPassword(),
//                     joinForm.getEmail());
//         });
//         // Then
//         assertEquals(LOGIN_ID_OR_EMAIL_OVERLAP.getMessage(), exception.getMessage());
//     }
//
//     @DisplayName("회원 가입 테스트 - 아이디 중복일 시(휴면 유저 테이블)")
//     @Test
//     void 회원_가입_테스트_아이디_중복일_시_휴면_유저테이블에서_중복() {
//         // Given
//         final String inputEmail = "18018008@gmail.com";
//         final String inputLoginId = "diger";
//         final String inputPassword = "qwer1234!";
//         final JoinForm joinForm = new JoinForm(inputLoginId, inputPassword, inputEmail);
//
//         // When
//         when(userIsolationRepository.findByLoginId(joinForm.getLoginId()))
//                 .thenReturn(Optional.of(new UserIsolation()));
//
//         Throwable exception = assertThrows(RuntimeException.class, () -> {
//             userBusinessService.executeJoin(
//                     joinForm.getLoginId(),
//                     joinForm.getPassword(),
//                     joinForm.getEmail());
//         });
//         // Then
//         assertEquals(LOGIN_ID_OR_EMAIL_OVERLAP.getMessage(), exception.getMessage());
//     }
//
//     @DisplayName("회원 가입 테스트 - 이메일 중복일 시(일반 유저 테이블)")
//     @Test
//     void 회원_가입_테스트_이메일_중복일_시() {
//         // Given
//         final String inputEmail = "18018008@gmail.com";
//         final String inputLoginId = "diger";
//         final String inputPassword = "qwer1234!";
//         final JoinForm joinForm = new JoinForm(inputLoginId, inputPassword, inputEmail);
//
//         // When
//         when(userRepository.findByEmail(joinForm.getEmail()))
//                 .thenReturn(Optional.of(new User()));
//
//         Throwable exception = assertThrows(RuntimeException.class, () -> {
//             userBusinessService.executeJoin(
//                     joinForm.getLoginId(),
//                     joinForm.getPassword(),
//                     joinForm.getEmail());
//         });
//         // Then
//         assertEquals(LOGIN_ID_OR_EMAIL_OVERLAP.getMessage(), exception.getMessage());
//     }
//
//     @DisplayName("회원 가입 테스트 - 이메일 중복일 시(휴면 유저 테이블)")
//     @Test
//     void 회원_가입_테스트_이메일_중복일_시_휴면_유저테이블에서_중복() {
//         // Given
//         final String inputEmail = "18018008@gmail.com";
//         final String inputLoginId = "diger";
//         final String inputPassword = "qwer1234!";
//         final JoinForm joinForm = new JoinForm(inputLoginId, inputPassword, inputEmail);
//
//         // When
//         when(userIsolationRepository.findByEmail(joinForm.getEmail()))
//                 .thenReturn(Optional.of(new UserIsolation()));
//
//         Throwable exception = assertThrows(RuntimeException.class, () -> {
//             userBusinessService.executeJoin(
//                     joinForm.getLoginId(),
//                     joinForm.getPassword(),
//                     joinForm.getEmail());
//         });
//         // Then
//         assertEquals(LOGIN_ID_OR_EMAIL_OVERLAP.getMessage(), exception.getMessage());
//     }
//
//     @DisplayName("회원 가입 테스트 - 회원가입 성공")
//     @Test
//     public void 회원_가입_테스트_회원가입_성공() {
//         // Given
//         final String inputEmail = "18018008@suwon.ac.kr";
//         final String inputLoginId = "diger";
//         final String inputPassword = "qwer1234!";
//         final JoinForm joinForm = new JoinForm(inputLoginId, inputPassword, inputEmail);
//
//         // When
//         Map<String, Boolean> result = userBusinessService.executeJoin(
//                 joinForm.getLoginId(),
//                 joinForm.getPassword(),
//                 joinForm.getEmail()
//         );
//
//         // Then
//         assertThat(result).isEqualTo(successFlag());
//     }
//
//     @DisplayName("아이디 찾기 테스트 - 해당 이메일로 아이디를 찾을 수 없을 때")
//     @Test
//     void 아이디_찾기_테스트_해당_이메일로_아이디를_찾을_수_없을_때() {
//         // Given
//         final String inputEmail = "18018008@gmail.com";
//         final FindIdForm findIdForm = new FindIdForm(inputEmail);
//
//         // When
//         when(userRepository.findByEmail(findIdForm.getEmail()))
//                 .thenReturn(Optional.empty());
//         when(userIsolationRepository.findByEmail(findIdForm.getEmail()))
//                 .thenReturn(Optional.empty());
//
//         Throwable exception = assertThrows(RuntimeException.class, () -> {
//             userBusinessService.executeFindId(findIdForm.getEmail());
//         });
//
//         // Then
//         assertEquals(USER_NOT_EXISTS.getMessage(), exception.getMessage());
//     }
//
//     @DisplayName("아이디 찾기 테스트 - 성공")
//     @Test
//     void 아이디_찾기_테스트_성공() {
//         // Given
//         final String inputEmail = "18018008@suwon.ac.kr";
//         final FindIdForm findIdForm = new FindIdForm(inputEmail);
//         final User foundedUser = User.builder().email(findIdForm.getEmail()).build();
//
//         // When
//         when(userRepository.findByEmail(findIdForm.getEmail()))
//                 .thenReturn(Optional.of(foundedUser));
//         Map<String, Boolean> result = userBusinessService.executeFindId(findIdForm.getEmail());
//
//         // Then
//         assertThat(foundedUser.getEmail()).isEqualTo(findIdForm.getEmail());
//         assertThat(result).isEqualTo(successFlag());
//     }
//
//     @DisplayName("비밀번호 찾기 테스트 - 로그인 아이디로 해당 유저를 찾을 수 없을 때")
//     @Test
//     void 비밀번호_찾기_테스트_로그인_아이디로_해당_유저를_찾을_수_없을_때() {
//         // Given
//         final String inputLoginId = "diger";
//         final String inputEmail = "18018008@suwon.ac.kr";
//         final FindPasswordForm findPasswordForm = new FindPasswordForm(inputLoginId, inputEmail);
//
//         // When
//         when(userRepository.findByLoginId(findPasswordForm.getLoginId()))
//                 .thenReturn(Optional.empty());
//         when(userIsolationRepository.findByLoginId(findPasswordForm.getLoginId()))
//                 .thenReturn(Optional.empty());
//
//         Throwable exception = assertThrows(RuntimeException.class, () -> {
//             userBusinessService.executeFindPw(findPasswordForm.getLoginId(), findPasswordForm.getEmail());
//         });
//
//         // Then
//         assertEquals(USER_NOT_FOUND.getMessage(), exception.getMessage());
//     }
//
//     @DisplayName("비밀번호 찾기 테스트 - 이메일로 해당 유저를 찾을 수 없을 때")
//     @Test
//     void 비밀번호_찾기_테스트_이메일로_해당_유저를_찾을_수_없을_때() {
//         // Given
//         final String inputLoginId = "diger";
//         final String inputEmail = "18018008@suwon.ac.kr";
//         final FindPasswordForm findPasswordForm = new FindPasswordForm(inputLoginId, inputEmail);
//
//         // When
//         when(userRepository.findByEmail(findPasswordForm.getEmail()))
//                 .thenReturn(Optional.empty());
//         when(userIsolationRepository.findByEmail(findPasswordForm.getEmail()))
//                 .thenReturn(Optional.empty());
//
//         Throwable exception = assertThrows(RuntimeException.class, () -> {
//             userBusinessService.executeFindPw(findPasswordForm.getLoginId(), findPasswordForm.getEmail());
//         });
//
//         // Then
//         assertEquals(USER_NOT_FOUND.getMessage(), exception.getMessage());
//     }
//
//     @DisplayName("비밀번호 찾기 테스트 - 성공 유저 테이블에 존재할 시")
//     @Test
//     void 비밀번호_찾기_테스트_성공_유저_테이블에_존재할_시() {
//         // Given
//         final String inputLoginId = "diger";
//         final String inputEmail = "18018008@suwon.ac.kr";
//         final FindPasswordForm findPasswordForm = new FindPasswordForm(inputLoginId, inputEmail);
//         final User user = User.builder()
//                 .loginId(findPasswordForm.getLoginId())
//                 .email(findPasswordForm.getEmail())
//                 .build();
//
//         // When
//         when(userRepository.findByLoginId(findPasswordForm.getLoginId()))
//                 .thenReturn(Optional.ofNullable(user));
//         when(userRepository.findByEmail(findPasswordForm.getEmail()))
//                 .thenReturn(Optional.ofNullable(user));
//
//         Map<String, Boolean> result = userBusinessService.executeFindPw(inputLoginId, inputEmail);
//
//         // Then
//         assertThat(result).isEqualTo(successFlag());
//     }
//
//     @DisplayName("비밀번호 찾기 테스트 - 성공, 휴면유저 테이블에 존재할 시")
//     @Test
//     void 비밀번호_찾기_테스트_성공_휴면_유저_테이블에_존재할_시() {
//         // Given
//         final String inputLoginId = "diger";
//         final String inputEmail = "18018008@suwon.ac.kr";
//         final FindPasswordForm findPasswordForm = new FindPasswordForm(inputLoginId, inputEmail);
//         final UserIsolation userIsolation = UserIsolation.builder()
//                 .loginId(findPasswordForm.getLoginId())
//                 .email(findPasswordForm.getEmail())
//                 .build();
//
//         // When
//         when(userIsolationRepository.findByLoginId(findPasswordForm.getLoginId()))
//                 .thenReturn(Optional.ofNullable(userIsolation));
//         when(userIsolationRepository.findByEmail(findPasswordForm.getEmail()))
//                 .thenReturn(Optional.ofNullable(userIsolation));
//
//         Map<String, Boolean> result = userBusinessService.executeFindPw(inputLoginId, inputEmail);
//
//         // Then
//         assertThat(result).isEqualTo(successFlag());
//     }
//
//     @DisplayName("로그인 테스트 - 유저를 찾을 수 없음 (비밀번호를 찾을 수 없음)")
//     @Test
//     void 로그인_테스트_유저를_찾을_수_없음() {
//         // Given
//         final String inputLoginId = "diger";
//         final String password = "qwer1234!";
//         final LoginForm loginForm = new LoginForm(inputLoginId, password);
//
//         // When
//         when(userRepository.findByLoginId(loginForm.getLoginId()))
//                 .thenReturn(Optional.empty());
//         when(userIsolationRepository.findByLoginId(loginForm.getLoginId()))
//                 .thenReturn(Optional.empty());
//
//         Throwable exception = assertThrows(RuntimeException.class, () -> {
//             userBusinessService.executeLogin(loginForm.getLoginId(), loginForm.getPassword());
//         });
//
//         // Then
//         assertEquals(PASSWORD_ERROR.getMessage(), exception.getMessage());
//     }
//
//     @DisplayName("로그인 테스트 - 이메일 인증을 수행하지 않음")
//     @Test
//     void 로그인_테스트_이메일_인증을_수행하지_않음() {
//         // Given
//         final String inputLoginId = "diger";
//         final String password = "qwer1234!";
//         final LoginForm loginForm = new LoginForm(inputLoginId, password);
//         final User user = User.builder()
//                 .loginId(loginForm.getLoginId())
//                 .password("testPassw0!rd")
//                 .build();
//
//         // When
//         when(userRepository.findByLoginId(loginForm.getLoginId()))
//                 .thenReturn(Optional.ofNullable(user));
//
//         Throwable exception = assertThrows(RuntimeException.class, () -> {
//             userBusinessService.executeLogin(loginForm.getLoginId(), loginForm.getPassword());
//         });
//
//         // Then
//         assertEquals(USER_NOT_EMAIL_AUTHED.getMessage(), exception.getMessage());
//     }
//
//     @DisplayName("로그인 테스트 - 유저테이블에서 비밀번호가 일치하지 않음")
//     @Test
//     void 로그인_테스트_유저테이블에서_비밀번호가_일치하지_않음() {
//         // Given
//         final String inputLoginId = "diger";
//         final String password = "qwer1234!";
//         final LoginForm loginForm = new LoginForm(inputLoginId, password);
//         final User user = User.builder()
//                 .loginId(loginForm.getLoginId())
//                 .password("testPassw0!rd")
//                 .restricted(false)
//                 .build();
//         final ConfirmationToken confirmationToken = ConfirmationToken.builder()
//                 .token("blah blah")
//                 .confirmedAt(LocalDateTime.now())
//                 .build();
//
//         // When
//         when(userRepository.findByLoginId(loginForm.getLoginId()))
//                 .thenReturn(Optional.ofNullable(user));
//         when(confirmationTokenRepository.findByUserIdx(user.getId()))
//                 .thenReturn(Optional.of(confirmationToken));
//
//         Throwable exception = assertThrows(RuntimeException.class, () -> {
//             userBusinessService.executeLogin(loginForm.getLoginId(), loginForm.getPassword());
//         });
//
//         // Then
//         assertEquals(PASSWORD_ERROR.getMessage(), exception.getMessage());
//     }
//
//     @DisplayName("로그인 테스트 - 성공")
//     @Test
//     void 로그인_테스트_성공() {
//         // Given
//         final String inputLoginId = "diger";
//         final String inputPassword = "qwer1234!";
//         final LoginForm loginForm = new LoginForm(inputLoginId, inputPassword);
//         final User user = User.builder()
//                 .loginId(loginForm.getLoginId())
//                 .password(loginForm.getPassword())
//                 .build();
//         final ConfirmationToken confirmationToken = ConfirmationToken.builder()
//                 .token("blah blah")
//                 .confirmedAt(LocalDateTime.now())
//                 .build();
//
//         // When
//         when(userRepository.findByLoginId(loginForm.getLoginId()))
//                 .thenReturn(Optional.ofNullable(user));
//         when(userIsolationRepository.findByLoginId(loginForm.getLoginId()))
//                 .thenReturn(Optional.empty());
//         when(confirmationTokenRepository.findByUserIdx(Objects.requireNonNull(user).getId()))
//                 .thenReturn(Optional.of(confirmationToken));
//         when(bCryptPasswordEncoder.matches(loginForm.getPassword(), user.getPassword()))
//                 .thenReturn(true);
//         Map<String, String> result = userBusinessService.executeLogin(
//                 loginForm.getLoginId(),
//                 loginForm.getPassword()
//         );
//
//         // Then
//         assertThat(result).isInstanceOf(HashMap.class);
//     }
// }