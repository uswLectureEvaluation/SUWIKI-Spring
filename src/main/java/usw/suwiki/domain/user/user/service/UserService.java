package usw.suwiki.domain.user.user.service;

import static usw.suwiki.global.exception.ErrorType.IS_NOT_EMAIL_FORM;
import static usw.suwiki.global.exception.ErrorType.PASSWORD_ERROR;
import static usw.suwiki.global.exception.ErrorType.PASSWORD_NOT_CHANGED;
import static usw.suwiki.global.exception.ErrorType.USER_AND_EMAIL_OVERLAP;
import static usw.suwiki.global.exception.ErrorType.USER_NOT_EXISTS;
import static usw.suwiki.global.exception.ErrorType.USER_NOT_FOUND;
import static usw.suwiki.global.exception.ErrorType.USER_RESTRICTED;
import static usw.suwiki.global.util.apiresponse.ApiResponseFactory.overlapFalseFlag;
import static usw.suwiki.global.util.apiresponse.ApiResponseFactory.overlapTrueFlag;
import static usw.suwiki.global.util.apiresponse.ApiResponseFactory.successFlag;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.blacklistdomain.BlackListService;
import usw.suwiki.domain.confirmationtoken.entity.ConfirmationToken;
import usw.suwiki.domain.confirmationtoken.repository.ConfirmationTokenRepository;
import usw.suwiki.domain.confirmationtoken.service.ConfirmationTokenService;
import usw.suwiki.domain.confirmationtoken.service.EmailSender;
import usw.suwiki.domain.evaluation.entity.EvaluatePosts;
import usw.suwiki.domain.evaluation.repository.EvaluatePostsRepository;
import usw.suwiki.domain.evaluation.service.EvaluatePostsService;
import usw.suwiki.domain.exam.entity.ExamPosts;
import usw.suwiki.domain.exam.repository.ExamPostsRepository;
import usw.suwiki.domain.exam.service.ExamPostsService;
import usw.suwiki.domain.favoritemajor.service.FavoriteMajorService;
import usw.suwiki.domain.postreport.entity.EvaluatePostReport;
import usw.suwiki.domain.postreport.entity.ExamPostReport;
import usw.suwiki.domain.postreport.repository.EvaluateReportRepository;
import usw.suwiki.domain.postreport.repository.ExamReportRepository;
import usw.suwiki.domain.postreport.service.PostReportService;
import usw.suwiki.domain.refreshToken.repository.RefreshTokenRepository;
import usw.suwiki.domain.user.user.dto.UserRequestDto.EvaluateReportForm;
import usw.suwiki.domain.user.user.dto.UserRequestDto.ExamReportForm;
import usw.suwiki.domain.user.user.dto.UserResponseDto.MyPageForm;
import usw.suwiki.domain.user.user.entity.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.domain.user.userIsolation.repository.UserIsolationRepository;
import usw.suwiki.domain.user.userIsolation.service.UserIsolationService;
import usw.suwiki.domain.viewExam.service.ViewExamService;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtTokenProvider;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.jwt.JwtTokenValidator;
import usw.suwiki.global.util.emailBuild.BuildEmailAuthForm;
import usw.suwiki.global.util.emailBuild.BuildFindLoginIdForm;
import usw.suwiki.global.util.emailBuild.BuildFindPasswordForm;


@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final String BASE_LINK = "https://api.suwiki.kr/user/verify-email/?token=";
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final UserIsolationRepository userIsolationRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EvaluatePostsRepository evaluatePostsRepository;
    private final ExamPostsRepository examPostsRepository;
    private final EvaluateReportRepository evaluateReportRepository;
    private final ExamReportRepository examReportRepository;
    private final EmailSender emailSender;
    private final ConfirmationTokenService confirmationTokenService;
    private final BuildEmailAuthForm buildEmailAuthForm;
    private final BuildFindLoginIdForm BuildFindLoginIdForm;
    private final BuildFindPasswordForm BuildFindPasswordForm;
    private final BlackListService blackListService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenResolver jwtTokenResolver;
    private final UserIsolationService userIsolationService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final FavoriteMajorService favoriteMajorService;
    private final ViewExamService viewExamService;
    private final EvaluatePostsService evaluatePostsService;
    private final ExamPostsService examPostsService;
    private final PostReportService postReportService;

    @Transactional(readOnly = true)
    public Map<String, Boolean> executeCheckId(String loginId) {
        if (userRepository.findByLoginId(loginId).isPresent() ||
            userIsolationRepository.findByLoginId(loginId).isPresent()) {
            return overlapTrueFlag();
        }
        return overlapFalseFlag();
    }

    @Transactional(readOnly = true)
    public Map<String, Boolean> executeCheckEmail(String email) {
        if (userRepository.findByEmail(email).isPresent() ||
            userIsolationRepository.findByEmail(email).isPresent()) {
            return overlapTrueFlag();
        }
        return overlapFalseFlag();
    }

    @Transactional
    public Map<String, Boolean> executeJoin(String loginId, String password, String email) {
        blackListService.joinRequestUserIsBlackList(email);

        if (userRepository.findByLoginId(loginId).isPresent() ||
            userIsolationRepository.findByLoginId(loginId).isPresent() ||
            userRepository.findByEmail(email).isPresent() ||
            userIsolationRepository.findByEmail(email).isPresent()) {
            throw new AccountException(USER_AND_EMAIL_OVERLAP);
        }

        if (!email.contains("@suwon.ac.kr")) {
            throw new AccountException(IS_NOT_EMAIL_FORM);
        }

        User user = User.makeUser(loginId, bCryptPasswordEncoder.encode(password), email);
        userRepository.save(user);

        ConfirmationToken confirmationToken = ConfirmationToken.makeToken(user);
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        emailSender.send(email, buildEmailAuthForm
            .buildEmail(BASE_LINK + confirmationToken.getToken())
        );
        return successFlag();
    }

    public String executeVerifyEmail(String token) {
        return confirmationTokenService.confirmToken(token);
    }

    public Map<String, Boolean> executeFindId(String email) {
        Optional<User> requestUser = userRepository.findByEmail(email);
        if (requestUser.isPresent()) {
            emailSender.send(email,
                BuildFindLoginIdForm.buildEmail(requestUser.get().getLoginId()));
            return successFlag();
        }
        throw new AccountException(USER_NOT_EXISTS);
    }

    public Map<String, Boolean> executeFindPw(String loginId, String email) {
        Optional<User> userByLoginId = userRepository.findByLoginId(loginId);
        Optional<User> userByEmail = userRepository.findByEmail(email);
        User user;
        if (userByLoginId.equals(userByEmail) &&
            userByLoginId.isPresent() &&
            userByEmail.isPresent())
        {
            user = userByLoginId.get();
            emailSender.send(email, BuildFindPasswordForm.buildEmail(
                    user.updateRandomPassword(bCryptPasswordEncoder)
                )
            );
            return successFlag();
        }
        throw new AccountException(USER_NOT_FOUND);
    }

    public Map<String, String> executeLogin(String loginId, String password) {
        Map<String, String> tokenPair = new HashMap<>();
        if (userIsolationRepository.findByLoginId(loginId).isEmpty()) {
            User notSleepingUser = loadUserFromLoginId(loginId);
            notSleepingUser.isUserEmailAuthed(
                confirmationTokenRepository.findByUserIdx(notSleepingUser.getId())
            );
            if (matchPassword(loginId, password)) {
                tokenPair.put(
                    "AccessToken", jwtTokenProvider.createAccessToken(notSleepingUser)
                );
                tokenPair.put(
                    "RefreshToken", jwtTokenResolver.refreshTokenUpdateOrCreate(notSleepingUser)
                );
                notSleepingUser.updateLastLoginDate();
                return tokenPair;
            }
        } else if (userIsolationRepository.findByLoginId(loginId).isPresent()) {
            User sleepingUser = userIsolationService.sleepingUserLogin(loginId, password);
            tokenPair.put(
                "AccessToken", jwtTokenProvider.createAccessToken(sleepingUser)
            );
            tokenPair.put(
                "RefreshToken", jwtTokenResolver.refreshTokenUpdateOrCreate(sleepingUser)
            );
            sleepingUser.updateLastLoginDate();
            return tokenPair;
        }
        throw new AccountException(PASSWORD_ERROR);
    }

    public Map<String, Boolean> executeEditPassword(
        User user, String prePassword, String newPassword) {
        if (prePassword.equals(newPassword)) {
            throw new AccountException(PASSWORD_NOT_CHANGED);
        } else if (!matchPassword(user.getLoginId(), prePassword)) {
            throw new AccountException(PASSWORD_ERROR);
        }
        user.updatePassword(bCryptPasswordEncoder, newPassword);
        return successFlag();
    }

    public MyPageForm executeLoadMyPage(String Authorization) {
        Long userIdx = jwtTokenResolver.getId(Authorization);
        User user = loadUserFromUserIdx(userIdx);
        return MyPageForm.builder()
            .loginId(user.getLoginId())
            .email(user.getEmail())
            .point(user.getPoint())
            .writtenEvaluation(user.getWrittenEvaluation())
            .writtenExam(user.getWrittenExam())
            .viewExam(user.getViewExamCount())
            .build();
    }

    public Map<String, String> executeJWTRefreshForWebClient(Cookie requestRefreshCookie) {
        String refreshToken = requestRefreshCookie.getValue();
        if (refreshTokenRepository.findByPayload(refreshToken).isEmpty()) {
            throw new AccountException(USER_RESTRICTED);
        }
        Long userIdx = refreshTokenRepository.findByPayload(refreshToken).get().getUserIdx();
        User user = loadUserFromUserIdx(userIdx);
        return new HashMap<>() {{
            put("AccessToken", jwtTokenProvider.createAccessToken(user));
            put("RefreshToken", jwtTokenResolver.refreshTokenUpdateOrCreate(user));
        }};
    }

    public Map<String, String> executeJWTRefreshForMobileClient(String Authorization) {
        if (refreshTokenRepository.findByPayload(Authorization).isEmpty()) {
            throw new AccountException(USER_RESTRICTED);
        }
        Long userIdx = refreshTokenRepository.findByPayload(Authorization).get().getUserIdx();
        User user = loadUserFromUserIdx(userIdx);
        return new HashMap<>() {{
            put("AccessToken", jwtTokenProvider.createAccessToken(user));
            put("RefreshToken", jwtTokenResolver.refreshTokenUpdateOrCreate(user));
        }};
    }

    public Map<String, Boolean> executeQuit(String Authorization, String inputPassword) {
        jwtTokenValidator.validateAccessToken(Authorization);
        User user = loadUserFromUserIdx(jwtTokenResolver.getId(Authorization));
        if (user.validatePassword(bCryptPasswordEncoder, inputPassword)) {
            throw new AccountException(USER_NOT_EXISTS);
        }
        postReportService.deleteByUserIdx(user.getId());
        favoriteMajorService.deleteAllByUser(user.getId());
        viewExamService.deleteByUserIdx(user.getId());
        examPostsService.deleteByUser(user.getId());
        evaluatePostsService.deleteByUser(user.getId());
        user.disable();
        return successFlag();
    }

    public boolean matchPassword(String loginId, String inputPassword) {
        return bCryptPasswordEncoder.matches(inputPassword,
            userRepository.findByLoginId(loginId).get().getPassword());
    }

    public User convertOptionalUserToDomainUser(Optional<User> optionalUser) {
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        throw new AccountException(USER_NOT_EXISTS);
    }

    public User loadUserFromUserIdx(Long userIdx) {
        return convertOptionalUserToDomainUser(userRepository.findById(userIdx));
    }

    public User loadUserFromLoginId(String loginId) {
        return convertOptionalUserToDomainUser(userRepository.findByLoginId(loginId));
    }

    public EvaluatePosts loadEvaluatePostsByIndex(Long EvaluatePostsIdx) {
        return evaluatePostsRepository.findById(EvaluatePostsIdx);
    }

    public ExamPosts loadExamPostsByIndex(Long ExamPostsIdx) {
        return examPostsRepository.findById(ExamPostsIdx);
    }

    public void reportExamPost(
        ExamReportForm userReportForm, Long reportingUserIdx
    ) {
        Long reportTargetUser = loadExamPostsByIndex(userReportForm.getExamIdx()).getUser().getId();
        ExamPosts reportedTargetPost = loadExamPostsByIndex(userReportForm.getExamIdx());
        ExamPostReport target = ExamPostReport.builder()
            .examIdx(userReportForm.getExamIdx())
            .lectureName(reportedTargetPost.getLectureName())
            .professor(reportedTargetPost.getProfessor())
            .content(reportedTargetPost.getContent())
            .reportedUserIdx(reportTargetUser)
            .reportingUserIdx(reportingUserIdx)
            .reportedDate(LocalDateTime.now())
            .build();
        examReportRepository.save(target);
    }

    public void reportEvaluatePost(
        EvaluateReportForm userReportForm, Long reportingUserIdx
    ) {
        Long reportTargetUser = loadEvaluatePostsByIndex(userReportForm.getEvaluateIdx()).getUser()
            .getId();
        EvaluatePosts reportTargetPost = loadEvaluatePostsByIndex(userReportForm.getEvaluateIdx());
        EvaluatePostReport target = EvaluatePostReport.builder()
            .evaluateIdx(userReportForm.getEvaluateIdx())
            .lectureName(reportTargetPost.getLectureName())
            .professor(reportTargetPost.getProfessor())
            .content(reportTargetPost.getContent())
            .reportedUserIdx(reportTargetUser)
            .reportingUserIdx(reportingUserIdx)
            .reportedDate(LocalDateTime.now())
            .build();
        evaluateReportRepository.save(target);
    }

    public Long whoIsEvaluateReporting(Long evaluateIdx) {
        return evaluateReportRepository.findByEvaluateIdx(evaluateIdx).get().getReportingUserIdx();
    }

    public Long whoIsExamReporting(Long examIdx) {
        return examReportRepository.findByExamIdx(examIdx).get().getReportingUserIdx();
    }
}