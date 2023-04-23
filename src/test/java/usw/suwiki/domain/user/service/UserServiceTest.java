package usw.suwiki.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static usw.suwiki.global.exception.ErrorType.IS_NOT_EMAIL_FORM;
import static usw.suwiki.global.exception.ErrorType.USER_AND_EMAIL_OVERLAP;
import static usw.suwiki.global.exception.ErrorType.USER_NOT_EXISTS;
import static usw.suwiki.global.util.apiresponse.ApiResponseFactory.overlapFalseFlag;
import static usw.suwiki.global.util.apiresponse.ApiResponseFactory.overlapTrueFlag;
import static usw.suwiki.global.util.apiresponse.ApiResponseFactory.successFlag;

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import usw.suwiki.domain.blacklistdomain.BlackListService;
import usw.suwiki.domain.confirmationtoken.repository.ConfirmationTokenRepository;
import usw.suwiki.domain.confirmationtoken.service.ConfirmationTokenService;
import usw.suwiki.domain.confirmationtoken.service.EmailSender;
import usw.suwiki.domain.evaluation.repository.EvaluatePostsRepository;
import usw.suwiki.domain.evaluation.service.EvaluatePostsService;
import usw.suwiki.domain.exam.repository.ExamPostsRepository;
import usw.suwiki.domain.exam.service.ExamPostsService;
import usw.suwiki.domain.favoritemajor.service.FavoriteMajorService;
import usw.suwiki.domain.postreport.repository.EvaluateReportRepository;
import usw.suwiki.domain.postreport.repository.ExamReportRepository;
import usw.suwiki.domain.postreport.service.PostReportService;
import usw.suwiki.domain.refreshToken.repository.RefreshTokenRepository;
import usw.suwiki.domain.user.user.dto.UserRequestDto.CheckEmailForm;
import usw.suwiki.domain.user.user.dto.UserRequestDto.CheckLoginIdForm;
import usw.suwiki.domain.user.user.dto.UserRequestDto.FindIdForm;
import usw.suwiki.domain.user.user.dto.UserRequestDto.JoinForm;
import usw.suwiki.domain.user.user.entity.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.domain.user.user.service.UserService;
import usw.suwiki.domain.user.userIsolation.entity.UserIsolation;
import usw.suwiki.domain.user.userIsolation.repository.UserIsolationRepository;
import usw.suwiki.domain.user.userIsolation.service.UserIsolationService;
import usw.suwiki.domain.viewExam.service.ViewExamService;
import usw.suwiki.global.jwt.JwtTokenProvider;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.jwt.JwtTokenValidator;
import usw.suwiki.global.util.emailBuild.BuildEmailAuthForm;
import usw.suwiki.global.util.emailBuild.BuildFindLoginIdForm;
import usw.suwiki.global.util.emailBuild.BuildFindPasswordForm;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    UserRepository userRepository;
    @Mock
    UserIsolationRepository userIsolationRepository;
    @Mock
    ConfirmationTokenRepository confirmationTokenRepository;
    @Mock
    EvaluatePostsRepository evaluatePostsRepository;
    @Mock
    ExamPostsRepository examPostsRepository;
    @Mock
    EvaluateReportRepository evaluateReportRepository;
    @Mock
    ExamReportRepository examReportRepository;
    @Mock
    EmailSender emailSender;
    @Mock
    ConfirmationTokenService confirmationTokenService;
    @Mock
    BuildEmailAuthForm buildEmailAuthForm;
    @Mock
    BuildFindLoginIdForm BuildFindLoginIdForm;
    @Mock
    BuildFindPasswordForm BuildFindPasswordForm;
    @Mock
    BlackListService blackListService;
    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Mock
    JwtTokenValidator jwtTokenValidator;
    @Mock
    JwtTokenResolver jwtTokenResolver;
    @Mock
    UserIsolationService userIsolationService;
    @Mock
    RefreshTokenRepository refreshTokenRepository;
    @Mock
    FavoriteMajorService favoriteMajorService;
    @Mock
    ViewExamService viewExamService;
    @Mock
    EvaluatePostsService evaluatePostsService;
    @Mock
    ExamPostsService examPostsService;
    @Mock
    PostReportService postReportService;

    @InjectMocks
    UserService userService;

    @DisplayName("아이디 중복 확인 테스트 - 중복일 시")
    @Test
    public void 아이디_중복_확인_테스트_중복일_시() {
        // Given
        final String inputLoginId = "diger";
        final CheckLoginIdForm checkLoginIdForm = new CheckLoginIdForm(inputLoginId);
        final User user = User.builder().loginId(inputLoginId).build();

        // When
        when(userRepository.findByLoginId(checkLoginIdForm.getLoginId()))
            .thenReturn(Optional.ofNullable(user));
        Map<String, Boolean> result = userService.executeCheckId(inputLoginId);

        // Then
        assertThat(result).isEqualTo(overlapTrueFlag());
    }

    @DisplayName("아이디 중복 확인 테스트 - 중복이 아닐 시")
    @Test
    public void 아이디_중복_확인_테스트_중복이_아닐_시() {
        // Given
        final String inputLoginId = "diger";
        final CheckLoginIdForm checkLoginIdForm = new CheckLoginIdForm(inputLoginId);

        // When
        when(userRepository.findByLoginId(checkLoginIdForm.getLoginId()))
            .thenReturn(Optional.empty());
        Map<String, Boolean> result = userService.executeCheckId(inputLoginId);

        // Then
        assertThat(result).isEqualTo(overlapFalseFlag());
    }

    @DisplayName("이메일 중복 확인 테스트 - 중복일 시")
    @Test
    public void 이메일_중복_확인_테스트_중복일_시() {
        // Given
        final String inputEmail = "18018008@suwon.ac.kr";
        final CheckEmailForm checkEmailForm = new CheckEmailForm(inputEmail);
        final User user = User.builder().email(inputEmail).build();

        // When
        when(userRepository.findByEmail(checkEmailForm.getEmail()))
            .thenReturn(Optional.ofNullable(user));
        Map<String, Boolean> result = userService.executeCheckEmail(inputEmail);

        // Then
        assertThat(result).isEqualTo(overlapTrueFlag());
    }

    @DisplayName("이메일 중복 확인 테스트 - 중복이 아닐 시")
    @Test
    public void 이메일_중복_확인_테스트_중복이_아닐_시() {
        // Given
        final String inputEmail = "18018008@suwon.ac.kr";
        final CheckEmailForm checkEmailForm = new CheckEmailForm(inputEmail);

        // When
        when(userRepository.findByEmail(checkEmailForm.getEmail()))
            .thenReturn(Optional.empty());
        Map<String, Boolean> result = userService.executeCheckEmail(inputEmail);

        // Then
        assertThat(result).isEqualTo(overlapFalseFlag());
    }

    @DisplayName("회원 가입 테스트 - 학교 이메일 형식과 다를 경우")
    @Test
    void 회원_가입_테스트_학교_이메일_형식과_다를_경우() {
        // Given
        final String inputEmail = "18018008@gmail.com";
        final String inputLoginId = "diger";
        final String inputPassword = "qwer1234!";
        final JoinForm joinForm = new JoinForm(inputLoginId, inputPassword, inputEmail);

        // When
        Throwable exception = assertThrows(RuntimeException.class, () -> {
            userService.executeJoin(
                joinForm.getLoginId(),
                joinForm.getPassword(),
                joinForm.getEmail());
        });

        // Then
        assertEquals(IS_NOT_EMAIL_FORM.getMessage(), exception.getMessage());
    }

    @DisplayName("회원 가입 테스트 - 아이디 중복일 시(일반 유저 테이블)")
    @Test
    void 회원_가입_테스트_아이디_중복일_시() {
        // Given
        final String inputEmail = "18018008@gmail.com";
        final String inputLoginId = "diger";
        final String inputPassword = "qwer1234!";
        final JoinForm joinForm = new JoinForm(inputLoginId, inputPassword, inputEmail);

        // When
        when(userRepository.findByLoginId(joinForm.getLoginId()))
            .thenReturn(Optional.of(new User()));

        Throwable exception = assertThrows(RuntimeException.class, () -> {
            userService.executeJoin(
                joinForm.getLoginId(),
                joinForm.getPassword(),
                joinForm.getEmail());
        });
        // Then
        assertEquals(USER_AND_EMAIL_OVERLAP.getMessage(), exception.getMessage());
    }

    @DisplayName("회원 가입 테스트 - 아이디 중복일 시(휴면 유저 테이블)")
    @Test
    void 회원_가입_테스트_아이디_중복일_시_휴면_유저테이블에서_중복() {
        // Given
        final String inputEmail = "18018008@gmail.com";
        final String inputLoginId = "diger";
        final String inputPassword = "qwer1234!";
        final JoinForm joinForm = new JoinForm(inputLoginId, inputPassword, inputEmail);

        // When
        when(userIsolationRepository.findByLoginId(joinForm.getLoginId()))
            .thenReturn(Optional.of(new UserIsolation()));

        Throwable exception = assertThrows(RuntimeException.class, () -> {
            userService.executeJoin(
                joinForm.getLoginId(),
                joinForm.getPassword(),
                joinForm.getEmail());
        });
        // Then
        assertEquals(USER_AND_EMAIL_OVERLAP.getMessage(), exception.getMessage());
    }

    @DisplayName("회원 가입 테스트 - 이메일 중복일 시(일반 유저 테이블)")
    @Test
    void 회원_가입_테스트_이메일_중복일_시() {
        // Given
        final String inputEmail = "18018008@gmail.com";
        final String inputLoginId = "diger";
        final String inputPassword = "qwer1234!";
        final JoinForm joinForm = new JoinForm(inputLoginId, inputPassword, inputEmail);

        // When
        when(userRepository.findByEmail(joinForm.getEmail()))
            .thenReturn(Optional.of(new User()));

        Throwable exception = assertThrows(RuntimeException.class, () -> {
            userService.executeJoin(
                joinForm.getLoginId(),
                joinForm.getPassword(),
                joinForm.getEmail());
        });
        // Then
        assertEquals(USER_AND_EMAIL_OVERLAP.getMessage(), exception.getMessage());
    }

    @DisplayName("회원 가입 테스트 - 이메일 중복일 시(휴면 유저 테이블)")
    @Test
    void 회원_가입_테스트_이메일_중복일_시_휴면_유저테이블에서_중복() {
        // Given
        final String inputEmail = "18018008@gmail.com";
        final String inputLoginId = "diger";
        final String inputPassword = "qwer1234!";
        final JoinForm joinForm = new JoinForm(inputLoginId, inputPassword, inputEmail);

        // When
        when(userIsolationRepository.findByEmail(joinForm.getEmail()))
            .thenReturn(Optional.of(new UserIsolation()));

        Throwable exception = assertThrows(RuntimeException.class, () -> {
            userService.executeJoin(
                joinForm.getLoginId(),
                joinForm.getPassword(),
                joinForm.getEmail());
        });
        // Then
        assertEquals(USER_AND_EMAIL_OVERLAP.getMessage(), exception.getMessage());
    }

    @DisplayName("회원 가입 테스트 - 회원가입 성공")
    @Test
    public void 회원_가입_테스트_회원가입_성공() {
        // Given
        final String inputEmail = "18018008@suwon.ac.kr";
        final String inputLoginId = "diger";
        final String inputPassword = "qwer1234!";
        final JoinForm joinForm = new JoinForm(inputLoginId, inputPassword, inputEmail);

        // When
        Map<String, Boolean> result = userService.executeJoin(
            joinForm.getLoginId(),
            joinForm.getPassword(),
            joinForm.getEmail()
        );

        // Then
        assertThat(result).isEqualTo(successFlag());
    }

    @DisplayName("아이디 찾기 테스트 - 해당 이메일로 아이디를 찾을 수 없을 때")
    @Test
    void 아이디_찾기_테스트_해당_이메일로_아이디를_찾을_수_없을_때() {
        // Given
        final String inputEmail = "18018008@gmail.com";
        final FindIdForm findIdForm = new FindIdForm(inputEmail);

        // When
        when(userRepository.findByEmail(findIdForm.getEmail()))
            .thenReturn(Optional.empty());

        Throwable exception = assertThrows(RuntimeException.class, () -> {
            userService.executeFindId(findIdForm.getEmail());
        });

        // Then
        assertEquals(USER_NOT_EXISTS.getMessage(), exception.getMessage());
    }

    @DisplayName("아이디 찾기 테스트 - 성공")
    @Test
    void 아이디_찾기_테스트_성공() {
        // Given
        final String inputEmail = "18018008@suwon.ac.kr";
        final FindIdForm findIdForm = new FindIdForm(inputEmail);
        final User foundedUser = User.builder().email(findIdForm.getEmail()).build();

        // When
        when(userRepository.findByEmail(findIdForm.getEmail()))
            .thenReturn(Optional.of(foundedUser));
        Map<String, Boolean> result = userService.executeFindId(findIdForm.getEmail());

        // Then
        assertThat(foundedUser.getEmail()).isEqualTo(findIdForm.getEmail());
        assertThat(result).isEqualTo(successFlag());
    }
}