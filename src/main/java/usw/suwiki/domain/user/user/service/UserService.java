package usw.suwiki.domain.user.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.blacklistdomain.BlackListService;
import usw.suwiki.domain.confirmationtoken.entity.ConfirmationToken;
import usw.suwiki.domain.confirmationtoken.repository.ConfirmationTokenRepository;
import usw.suwiki.domain.evaluation.entity.EvaluatePosts;
import usw.suwiki.domain.evaluation.service.EvaluatePostsService;
import usw.suwiki.domain.exam.domain.ExamPosts;
import usw.suwiki.domain.exam.service.ExamPostsService;
import usw.suwiki.domain.favoritemajor.service.FavoriteMajorService;
import usw.suwiki.domain.postreport.entity.EvaluatePostReport;
import usw.suwiki.domain.postreport.entity.ExamPostReport;
import usw.suwiki.domain.postreport.service.ReportPostService;
import usw.suwiki.domain.refreshToken.entity.RefreshToken;
import usw.suwiki.domain.refreshToken.service.RefreshTokenService;
import usw.suwiki.domain.user.user.dto.UserRequestDto.EvaluateReportForm;
import usw.suwiki.domain.user.user.dto.UserRequestDto.ExamReportForm;
import usw.suwiki.domain.user.user.dto.UserResponseDto.MyPageResponseForm;
import usw.suwiki.domain.user.user.entity.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.domain.user.userIsolation.entity.UserIsolation;
import usw.suwiki.domain.user.userIsolation.repository.UserIsolationRepository;
import usw.suwiki.domain.viewExam.service.ViewExamService;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtProvider;
import usw.suwiki.global.jwt.JwtResolver;
import usw.suwiki.global.jwt.JwtValidator;
import usw.suwiki.global.mailsender.EmailSender;
import usw.suwiki.global.util.emailBuild.BuildEmailAuthForm;
import usw.suwiki.global.util.emailBuild.BuildFindLoginIdForm;
import usw.suwiki.global.util.emailBuild.BuildFindPasswordForm;

import javax.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static usw.suwiki.global.exception.ExceptionType.*;
import static usw.suwiki.global.util.apiresponse.ApiResponseFactory.*;


@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private static final String BASE_LINK = "https://api.suwiki.kr/user/verify-email/?token=";
    private static final String MAIL_FORM = "@suwon.ac.kr";
    private final UserRepository userRepository;
    private final UserIsolationRepository userIsolationRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailSender emailSender;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final BuildEmailAuthForm buildEmailAuthForm;
    private final BuildFindLoginIdForm BuildFindLoginIdForm;
    private final BuildFindPasswordForm BuildFindPasswordForm;
    private final BlackListService blackListService;
    private final JwtProvider jwtProvider;
    private final JwtValidator jwtValidator;
    private final JwtResolver jwtResolver;
    private final FavoriteMajorService favoriteMajorService;
    private final ViewExamService viewExamService;
    private final EvaluatePostsService evaluatePostsService;
    private final ExamPostsService examPostsService;
    private final ReportPostService reportPostService;

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
        blackListService.isUserInBlackListThatRequestJoin(email);

        if (userRepository.findByLoginId(loginId).isPresent() ||
                userIsolationRepository.findByLoginId(loginId).isPresent() ||
                userRepository.findByEmail(email).isPresent() ||
                userIsolationRepository.findByEmail(email).isPresent())
            throw new AccountException(USER_AND_EMAIL_OVERLAP);

        if (!email.contains(MAIL_FORM)) throw new AccountException(IS_NOT_EMAIL_FORM);

        User user = User.makeUser(loginId, bCryptPasswordEncoder.encode(password), email);
        userRepository.save(user);

        ConfirmationToken confirmationToken = ConfirmationToken.makeToken(user);
        confirmationTokenRepository.save(confirmationToken);

        emailSender.send(
                email,
                buildEmailAuthForm.buildEmail(BASE_LINK + confirmationToken.getToken())
        );
        return successFlag();
    }

    public Map<String, Boolean> executeFindId(String email) {
        Optional<User> requestUser = userRepository.findByEmail(email);
        Optional<UserIsolation> requestIsolationUser = userIsolationRepository.findByEmail(email);
        if (requestUser.isPresent()) {
            emailSender.send(
                    email,
                    BuildFindLoginIdForm.buildEmail(requestUser.get().getLoginId())
            );
            return successFlag();
        } else if (requestIsolationUser.isPresent()) {
            emailSender.send(
                    email,
                    BuildFindLoginIdForm.buildEmail(requestIsolationUser.get().getLoginId())
            );
            return successFlag();
        }
        throw new AccountException(USER_NOT_EXISTS);
    }

    public Map<String, Boolean> executeFindPw(String loginId, String email) {
        Optional<User> userByLoginId = userRepository.findByLoginId(loginId);
        Optional<User> userByEmail = userRepository.findByEmail(email);

        Optional<UserIsolation> isolationUserByLoginId =
                userIsolationRepository.findByLoginId(loginId);
        Optional<UserIsolation> isolationUserByEmail =
                userIsolationRepository.findByEmail(email);

        User user;
        UserIsolation userIsolation;

        if (userByLoginId.equals(userByEmail) &&
                userByLoginId.isPresent() &&
                userByEmail.isPresent()) {
            user = userByLoginId.get();
            emailSender.send(email, BuildFindPasswordForm.buildEmail(
                            user.updateRandomPassword(bCryptPasswordEncoder)
                    )
            );
            return successFlag();
        } else if (isolationUserByLoginId.equals(isolationUserByEmail) &&
                isolationUserByLoginId.isPresent() && isolationUserByEmail.isPresent()
        ) {
            userIsolation = isolationUserByEmail.get();
            emailSender.send(email, BuildFindPasswordForm.buildEmail(
                            userIsolation.updateRandomPassword(bCryptPasswordEncoder)
                    )
            );
            return successFlag();
        }
        throw new AccountException(USER_NOT_FOUND);
    }

    public Map<String, String> executeLogin(String loginId, String inputPassword) {
        Map<String, String> tokenPair = new HashMap<>();
        if (userRepository.findByLoginId(loginId).isPresent() &&
                userIsolationRepository.findByLoginId(loginId).isEmpty()) {
            User notSleepingUser = loadUserFromLoginId(loginId);
            notSleepingUser.isUserEmailAuthed(
                    confirmationTokenRepository.findByUserIdx(notSleepingUser.getId())
            );
            if (matchPassword(loginId, inputPassword)) {
                tokenPair.put(
                        "AccessToken", jwtProvider.createAccessToken(notSleepingUser)
                );
                tokenPair.put(
                        "RefreshToken",
                        jwtResolver.judgementRefreshTokenCreateOrUpdateInLogin(notSleepingUser)
                );
                notSleepingUser.updateLastLoginDate();
                return tokenPair;
            }
        } else if (userIsolationRepository.findByLoginId(loginId).isPresent() &&
                userRepository.findByLoginId(loginId).isEmpty()) {
            UserIsolation userIsolation = userIsolationRepository.findByLoginId(loginId).get();
            if (bCryptPasswordEncoder.matches(
                    inputPassword,
                    userIsolationRepository.findByLoginId(loginId).get().getPassword()
            )) {
                rollBackSoftDeletedForIsolation(userIsolation.getUserIdx());
                userIsolationRepository.deleteByLoginId(loginId);
            }

            User sleepingUser = loadUserFromLoginId(loginId);
            tokenPair.put(
                    "AccessToken", jwtProvider.createAccessToken(sleepingUser)
            );
            tokenPair.put(
                    "RefreshToken", jwtResolver.judgementRefreshTokenCreateOrUpdateInLogin(sleepingUser)
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

    public MyPageResponseForm executeLoadMyPage(String Authorization) {
        Long userIdx = jwtResolver.getId(Authorization);
        User user = loadUserFromUserIdx(userIdx);
        return MyPageResponseForm.builder()
                .loginId(user.getLoginId())
                .email(user.getEmail())
                .point(user.getPoint())
                .writtenEvaluation(user.getWrittenEvaluation())
                .writtenExam(user.getWrittenExam())
                .viewExam(user.getViewExamCount())
                .build();
    }

    public Map<String, String> executeJWTRefreshForWebClient(Cookie requestRefreshCookie) {
        String payload = requestRefreshCookie.getValue();
        RefreshToken refreshToken = refreshTokenService.loadRefreshTokenFromPayload(payload);
        User user = loadUserFromUserIdx(refreshToken.getUserIdx());
        return new HashMap<>() {{
            put("AccessToken",
                    jwtProvider.createAccessToken(user));
            put("RefreshToken",
                    jwtResolver.judgementRefreshTokenCreateOrUpdateInRefreshRequest(user));
        }};
    }

    public Map<String, String> executeJWTRefreshForMobileClient(String Authorization) {
        RefreshToken refreshToken = refreshTokenService.loadRefreshTokenFromPayload(Authorization);
        User user = loadUserFromUserIdx(refreshToken.getUserIdx());
        return new HashMap<>() {{
            put("AccessToken",
                    jwtProvider.createAccessToken(user));
            put("RefreshToken",
                    jwtResolver.judgementRefreshTokenCreateOrUpdateInRefreshRequest(user));
        }};
    }

    public Map<String, Boolean> executeQuit(String Authorization, String inputPassword) {
        jwtValidator.validateJwt(Authorization);
        User user = loadUserFromUserIdx(jwtResolver.getId(Authorization));
        if (user.validatePassword(bCryptPasswordEncoder, inputPassword)) {
            throw new AccountException(USER_NOT_EXISTS);
        }
        reportPostService.deleteFromUserIdx(user.getId());
        favoriteMajorService.deleteFromUserIdx(user.getId());
        viewExamService.deleteFromUserIdx(user.getId());
        examPostsService.deleteFromUserIdx(user.getId());
        evaluatePostsService.deleteFromUserIdx(user.getId());
        user.waitQuit();
        return successFlag();
    }

    public boolean matchPassword(String loginId, String inputPassword) {
        return bCryptPasswordEncoder.matches(
                inputPassword,
                userRepository.findByLoginId(loginId).get().getPassword()
        );
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

    public void reportExamPost(
            ExamReportForm userReportForm,
            Long reportingUserIdx
    ) {
        ExamPosts examPost = examPostsService.loadExamPostsFromExamPostsIdx(userReportForm.getExamIdx());
        Long reportTargetUser = examPost.getUser().getId();
        ExamPostReport target = ExamPostReport
                .builder()
                .examIdx(userReportForm.getExamIdx())
                .lectureName(examPost.getLectureName())
                .professor(examPost.getProfessor())
                .content(examPost.getContent())
                .reportedUserIdx(reportTargetUser)
                .reportingUserIdx(reportingUserIdx)
                .reportedDate(LocalDateTime.now())
                .build();
        reportPostService.saveExamPostReport(target);
    }

    public void reportEvaluatePost(
            EvaluateReportForm userReportForm,
            Long reportingUserIdx
    ) {
        EvaluatePosts evaluatePost = evaluatePostsService.loadEvaluatePostsFromEvaluatePostsIdx(userReportForm.getEvaluateIdx());
        Long reportTargetUser = evaluatePost.getUser().getId();
        EvaluatePostReport evaluatePostReport = EvaluatePostReport.builder()
                .evaluateIdx(userReportForm.getEvaluateIdx())
                .lectureName(evaluatePost.getLectureName())
                .professor(evaluatePost.getProfessor())
                .content(evaluatePost.getContent())
                .reportedUserIdx(reportTargetUser)
                .reportingUserIdx(reportingUserIdx)
                .reportedDate(LocalDateTime.now())
                .build();
        reportPostService.saveEvaluatePostReport(evaluatePostReport);
    }

    public void deleteFromUserIdx(Long userIdx) {
        userRepository.deleteById(userIdx);
    }

    public void softDeleteForIsolation(Long userIdx) {
        User user = loadUserFromUserIdx(userIdx);
        user.sleep();
    }

    public void rollBackSoftDeletedForIsolation(Long userIdx) {
        User user = loadUserFromUserIdx(userIdx);
        user.sleep();
    }

    public List<User> loadUsersLastLoginBeforeTargetTime(LocalDateTime targetTime) {
        return userRepository.findByLastLoginBefore(targetTime);
    }

    public int findAllUsersSize() {
        return userRepository.findAll().size();
    }
}