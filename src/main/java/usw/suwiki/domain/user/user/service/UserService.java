package usw.suwiki.domain.user.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.blacklistdomain.service.BlacklistDomainService;
import usw.suwiki.domain.admin.blacklistdomain.service.BlacklistDomainCRUDService;
import usw.suwiki.domain.admin.restrictinguser.service.RestrictingUserCRUDService;
import usw.suwiki.domain.confirmationtoken.ConfirmationToken;
import usw.suwiki.domain.confirmationtoken.repository.ConfirmationTokenRepository;
import usw.suwiki.domain.evaluation.entity.EvaluatePosts;
import usw.suwiki.domain.evaluation.service.EvaluatePostsService;
import usw.suwiki.domain.exam.domain.ExamPosts;
import usw.suwiki.domain.exam.service.ExamPostsService;
import usw.suwiki.domain.favoritemajor.dto.FavoriteSaveDto;
import usw.suwiki.domain.favoritemajor.service.FavoriteMajorService;
import usw.suwiki.domain.postreport.entity.EvaluatePostReport;
import usw.suwiki.domain.postreport.entity.ExamPostReport;
import usw.suwiki.domain.postreport.service.ReportPostService;
import usw.suwiki.domain.refreshToken.entity.RefreshToken;
import usw.suwiki.domain.refreshToken.service.RefreshTokenService;
import usw.suwiki.domain.user.user.controller.dto.UserRequestDto.EvaluateReportForm;
import usw.suwiki.domain.user.user.controller.dto.UserRequestDto.ExamReportForm;
import usw.suwiki.domain.user.user.controller.dto.UserResponseDto.LoadMyBlackListReasonResponseForm;
import usw.suwiki.domain.user.user.controller.dto.UserResponseDto.LoadMyRestrictedReasonResponseForm;
import usw.suwiki.domain.user.user.controller.dto.UserResponseDto.MyPageResponseForm;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.repository.UserRepository;
import usw.suwiki.domain.user.userIsolation.UserIsolation;
import usw.suwiki.domain.user.userIsolation.repository.UserIsolationRepository;
import usw.suwiki.domain.viewExam.service.ViewExamService;
import usw.suwiki.global.ResponseForm;
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
    private final UserCRUDService userCRUDService;
    private final UserRepository userRepository;
    private final UserIsolationRepository userIsolationRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailSender emailSender;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final BuildEmailAuthForm buildEmailAuthForm;
    private final BuildFindLoginIdForm BuildFindLoginIdForm;
    private final BuildFindPasswordForm BuildFindPasswordForm;
    private final BlacklistDomainService blacklistDomainService;
    private final JwtProvider jwtProvider;
    private final JwtValidator jwtValidator;
    private final JwtResolver jwtResolver;
    private final FavoriteMajorService favoriteMajorService;
    private final ViewExamService viewExamService;
    private final EvaluatePostsService evaluatePostsService;
    private final ExamPostsService examPostsService;
    private final ReportPostService reportPostService;
    private final BlacklistDomainCRUDService blacklistDomainCRUDService;
    private final RestrictingUserCRUDService restrictingUserCRUDService;

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
        blacklistDomainService.isUserInBlackListThatRequestJoin(email);

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
        if (userRepository.findByLoginId(loginId).isPresent() &&
                userIsolationRepository.findByLoginId(loginId).isEmpty()
        ) {
            User user = userCRUDService.loadUserFromLoginId(loginId);
            user.isUserEmailAuthed(confirmationTokenRepository.findByUserIdx(user.getId()));
            if (bCryptPasswordEncoder.matches(user.getPassword(), inputPassword)) {
                user.updateLastLoginDate();
                return generateUserJWT(user);
            }
        } else if (userIsolationRepository.findByLoginId(loginId).isPresent() &&
                userRepository.findByLoginId(loginId).isEmpty()
        ) {
            UserIsolation userIsolation = userIsolationRepository.findByLoginId(loginId).get();
            if (bCryptPasswordEncoder.matches(
                    inputPassword,
                    userIsolationRepository.findByLoginId(loginId).get().getPassword()
            )) {
                rollBackSoftDeletedForIsolation(userIsolation.getUserIdx());
                userIsolationRepository.deleteByLoginId(loginId);
            }
            User user = userCRUDService.loadUserFromLoginId(loginId);
            user.updateLastLoginDate();
            return generateUserJWT(user);
        }
        throw new AccountException(PASSWORD_ERROR);
    }

    public Map<String, Boolean> executeEditPassword(
            String Authorization, String prePassword, String newPassword
    ) {
        User user = userCRUDService.loadUserFromUserIdx(jwtResolver.getId(Authorization));
        if (prePassword.equals(newPassword)) {
            throw new AccountException(PASSWORD_NOT_CHANGED);
        } else if (!bCryptPasswordEncoder.matches(user.getPassword(), prePassword)) {
            throw new AccountException(PASSWORD_ERROR);
        }
        user.updatePassword(bCryptPasswordEncoder, newPassword);
        return successFlag();
    }

    public MyPageResponseForm executeLoadMyPage(String Authorization) {
        Long userIdx = jwtResolver.getId(Authorization);
        User user = userCRUDService.loadUserFromUserIdx(userIdx);
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
        User user = userCRUDService.loadUserFromUserIdx(refreshToken.getUserIdx());
        return new HashMap<>() {{
            put("AccessToken",
                    jwtProvider.createAccessToken(user));
            put("RefreshToken",
                    jwtResolver.judgementRefreshTokenCreateOrUpdateInRefreshRequest(user));
        }};
    }

    public Map<String, String> executeJWTRefreshForMobileClient(String Authorization) {
        RefreshToken refreshToken = refreshTokenService.loadRefreshTokenFromPayload(Authorization);
        User user = userCRUDService.loadUserFromUserIdx(refreshToken.getUserIdx());
        return new HashMap<>() {{
            put("AccessToken",
                    jwtProvider.createAccessToken(user));
            put("RefreshToken",
                    jwtResolver.judgementRefreshTokenCreateOrUpdateInRefreshRequest(user));
        }};
    }

    public Map<String, Boolean> executeQuit(String Authorization, String inputPassword) {
        jwtValidator.validateJwt(Authorization);
        User user = userCRUDService.loadUserFromUserIdx(jwtResolver.getId(Authorization));
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

    public Map<String, Boolean> executeReportEvaluatePost(
            EvaluateReportForm evaluateReportForm,
            String Authorization
    ) {
        jwtValidator.validateJwt(Authorization);
        if (jwtResolver.getUserIsRestricted(Authorization)) throw new AccountException(USER_RESTRICTED);
        Long reportingUserIdx = jwtResolver.getId(Authorization);
        EvaluatePosts evaluatePost = evaluatePostsService.loadEvaluatePostsFromEvaluatePostsIdx(
                evaluateReportForm.getEvaluateIdx());
        Long reportedUserIdx = evaluatePost.getUser().getId();

        reportPostService.saveEvaluatePostReport(EvaluatePostReport.builder()
                .evaluateIdx(evaluateReportForm.getEvaluateIdx())
                .lectureName(evaluatePost.getLectureName())
                .professor(evaluatePost.getProfessor())
                .content(evaluatePost.getContent())
                .reportedUserIdx(reportedUserIdx)
                .reportingUserIdx(reportingUserIdx)
                .reportedDate(LocalDateTime.now())
                .build());
        return successFlag();
    }

    public Map<String, Boolean> executeReportExamPost(
            ExamReportForm examReportForm,
            String Authorization
    ) {
        jwtValidator.validateJwt(Authorization);
        if (jwtResolver.getUserIsRestricted(Authorization)) throw new AccountException(USER_RESTRICTED);
        Long reportingUserIdx = jwtResolver.getId(Authorization);
        ExamPosts examPost = examPostsService.loadExamPostsFromExamPostsIdx(
                examReportForm.getExamIdx());
        Long reportedUserIdx = examPost.getUser().getId();
        ExamPostReport target = ExamPostReport
                .builder()
                .examIdx(examReportForm.getExamIdx())
                .lectureName(examPost.getLectureName())
                .professor(examPost.getProfessor())
                .content(examPost.getContent())
                .reportedUserIdx(reportedUserIdx)
                .reportingUserIdx(reportingUserIdx)
                .reportedDate(LocalDateTime.now())
                .build();
        reportPostService.saveExamPostReport(target);
        return successFlag();
    }

    public List<LoadMyBlackListReasonResponseForm> executeLoadBlackListReason(String Authorization) {
        jwtValidator.validateJwt(Authorization);
        User requestUser = userCRUDService.loadUserFromUserIdx(jwtResolver.getId(Authorization));
        return blacklistDomainCRUDService.loadAllBlacklistLog(requestUser.getId());
    }

    public List<LoadMyRestrictedReasonResponseForm> executeLoadRestrictedReason(String Authorization) {
        jwtValidator.validateJwt(Authorization);
        User requestUser = userCRUDService.loadUserFromUserIdx(jwtResolver.getId(Authorization));
        return restrictingUserCRUDService.loadRestrictedLog(requestUser.getId());
    }

    public void executeFavoriteMajorSave(String Authorization, FavoriteSaveDto favoriteSaveDto) {
        jwtValidator.validateJwt(Authorization);
        if (jwtResolver.getUserIsRestricted(Authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }
        Long userIdx = jwtResolver.getId(Authorization);
        favoriteMajorService.save(favoriteSaveDto, userIdx);

    }

    public void executeFavoriteMajorDelete(String Authorization, String majorType) {
        jwtValidator.validateJwt(Authorization);
        if (jwtResolver.getUserIsRestricted(Authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }
        Long userIdx = jwtResolver.getId(Authorization);
        favoriteMajorService.delete(userIdx, majorType);
    }

    public ResponseForm executeFavoriteMajorLoad(String Authorization) {
        jwtValidator.validateJwt(Authorization);
        if (jwtResolver.getUserIsRestricted(Authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }
        Long userIdx = jwtResolver.getId(Authorization);
        List<String> list = favoriteMajorService.findMajorTypeByUser(userIdx);
        return new ResponseForm(list);
    }

    public void deleteFromUserIdx(Long userIdx) {
        userRepository.deleteById(userIdx);
    }

    public void softDeleteForIsolation(Long userIdx) {
        User user = userCRUDService.loadUserFromUserIdx(userIdx);
        user.sleep();
    }

    public void rollBackSoftDeletedForIsolation(Long userIdx) {
        User user = userCRUDService.loadUserFromUserIdx(userIdx);
        user.sleep();
    }

    public List<User> loadUsersLastLoginBeforeTargetTime(LocalDateTime targetTime) {
        return userRepository.findByLastLoginBefore(targetTime);
    }

    private Map<String, String> generateUserJWT(User user) {
        return new HashMap<>() {{
            put("AccessToken", jwtProvider.createAccessToken(user));
            put("RefreshToken", jwtResolver.judgementRefreshTokenCreateOrUpdateInLogin(user));
        }};
    }
}