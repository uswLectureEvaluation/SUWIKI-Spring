package usw.suwiki.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
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
import usw.suwiki.domain.user.user.dto.UserRequestDto.CheckLoginIdForm;
import usw.suwiki.domain.user.user.entity.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.domain.user.user.service.UserService;
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
        final String inputLoginId = "kim";
        final CheckLoginIdForm checkLoginIdForm = new CheckLoginIdForm(inputLoginId);
        final User user = User.builder().loginId(inputLoginId).build();

        // When
        when(userRepository.findByLoginId(checkLoginIdForm.getLoginId()))
            .thenReturn(Optional.ofNullable(user));
        Map<String, Boolean> result = userService.executeCheckId(inputLoginId);

        // Then
        assertThat(user.getLoginId()).isEqualTo(inputLoginId);
        assertThat(result).isEqualTo(new HashMap<>() {{
            put("overlap", true);
        }});
    }

    @DisplayName("아이디 중복 확인 테스트 - 중복이 아닐 시")
    @Test
    public void 아이디_중복_확인_테스트_중복이_아닐_시() {
        // Given
        final String inputLoginId = "kim";
        final CheckLoginIdForm checkLoginIdForm = new CheckLoginIdForm(inputLoginId);
        final User user = User.builder().loginId(inputLoginId).build();

        // When
        when(userRepository.findByLoginId(checkLoginIdForm.getLoginId()))
            .thenReturn(Optional.empty());
        Map<String, Boolean> result = userService.executeCheckId(inputLoginId);

        // Then
        assertThat(user.getLoginId()).isNotEqualTo(checkLoginIdForm.getLoginId());
        assertThat(result).isEqualTo(new HashMap<>() {{
            put("overlap", favoriteMajorService);
        }});
    }
}
