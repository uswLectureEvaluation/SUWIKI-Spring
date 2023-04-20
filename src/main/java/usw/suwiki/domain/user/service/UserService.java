package usw.suwiki.domain.user.service;

import static usw.suwiki.global.exception.ErrorType.IS_NOT_EMAIL_FORM;
import static usw.suwiki.global.exception.ErrorType.PASSWORD_ERROR;
import static usw.suwiki.global.exception.ErrorType.PASSWORD_NOT_CHANGED;
import static usw.suwiki.global.exception.ErrorType.USER_AND_EMAIL_OVERLAP;
import static usw.suwiki.global.exception.ErrorType.USER_NOT_EMAIL_AUTHED;
import static usw.suwiki.global.exception.ErrorType.USER_NOT_EXISTS;
import static usw.suwiki.global.exception.ErrorType.USER_NOT_FOUND;
import static usw.suwiki.global.util.ApiResponseFactory.successFlag;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.blacklistdomain.BlackListService;
import usw.suwiki.domain.email.entity.ConfirmationToken;
import usw.suwiki.domain.email.repository.ConfirmationTokenRepository;
import usw.suwiki.domain.email.service.ConfirmationTokenService;
import usw.suwiki.domain.email.service.EmailAuthService;
import usw.suwiki.domain.email.service.EmailSender;
import usw.suwiki.domain.evaluation.entity.EvaluatePosts;
import usw.suwiki.domain.evaluation.repository.EvaluatePostsRepository;
import usw.suwiki.domain.exam.entity.ExamPosts;
import usw.suwiki.domain.exam.repository.ExamPostsRepository;
import usw.suwiki.domain.postreport.entity.EvaluatePostReport;
import usw.suwiki.domain.postreport.entity.ExamPostReport;
import usw.suwiki.domain.postreport.repository.EvaluateReportRepository;
import usw.suwiki.domain.postreport.repository.ExamReportRepository;
import usw.suwiki.domain.user.dto.UserRequestDto;
import usw.suwiki.domain.user.entity.User;
import usw.suwiki.domain.user.repository.UserRepository;
import usw.suwiki.domain.userIsolation.repository.UserIsolationRepository;
import usw.suwiki.domain.userIsolation.service.UserIsolationService;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtTokenProvider;
import usw.suwiki.global.jwt.JwtTokenResolver;
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
    private final EmailAuthService emailAuthService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenResolver jwtTokenResolver;
    private final UserIsolationService userIsolationService;

    public Map<String, Boolean> executeCheckId(String loginId) {
        if (userRepository.findByLoginId(loginId).isPresent() ||
            userIsolationRepository.findByLoginId(loginId).isPresent()) {
            return new HashMap<>() {{
                put("overlap", true);
            }};
        }
        return new HashMap<>() {{
            put("overlap", false);
        }};
    }

    public Map<String, Boolean> executeCheckEmail(String email) {
        if (userRepository.findByEmail(email).isPresent() ||
            userIsolationRepository.findByEmail(email).isPresent()) {
            return new HashMap<>() {{
                put("overlap", true);
            }};
        }
        return new HashMap<>() {{
            put("overlap", false);
        }};
    }

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
        ConfirmationToken confirmationToken = ConfirmationToken.makeToken(user);
        confirmationTokenService.saveConfirmationToken(ConfirmationToken.makeToken(user));
        emailSender.send(email, buildEmailAuthForm
            .buildEmail(BASE_LINK + confirmationToken.getToken())
        );
        return successFlag();
    }

    public String executeVerifyEmail(String token) {
        return emailAuthService.confirmToken(token);
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
        Optional<User> user = userRepository.findByLoginId(loginId);
        if (user.isPresent()) {
            emailSender.send(email,
                BuildFindPasswordForm.buildEmail(
                    user.get().updateRandomPassword(bCryptPasswordEncoder)
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
            if (notSleepingUser.validatePassword(password)) {
                tokenPair.put(
                    "AccessToken", jwtTokenProvider.createAccessToken(notSleepingUser)
                );
                tokenPair.put(
                    "RefreshToken", jwtTokenResolver.refreshTokenUpdateOrCreate(notSleepingUser)
                );
                notSleepingUser.updateLastLoginDate();
                return tokenPair;
            }
            throw new AccountException(PASSWORD_ERROR);
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
        } else if (!user.getPassword().equals(bCryptPasswordEncoder.encode(prePassword))) {
            throw new AccountException(PASSWORD_ERROR);
        }
        user.updatePassword(bCryptPasswordEncoder, newPassword);
        return successFlag();
    }

    public boolean validatePasswordAtUserTable(String loginId, String password) {
        return bCryptPasswordEncoder.matches(password,
            userRepository.findByLoginId(loginId).get().getPassword());
    }

    public void setLastLogin(User user) {
        userRepository.updateLastLogin(LocalDateTime.now(), user.getId());
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

    public void reportExamPost(UserRequestDto.ExamReportForm userReportForm,
        Long reportingUserIdx) {
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

    public void reportEvaluatePost(UserRequestDto.EvaluateReportForm userReportForm,
        Long reportingUserIdx) {
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