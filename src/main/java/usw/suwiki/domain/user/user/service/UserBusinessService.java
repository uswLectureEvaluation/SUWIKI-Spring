package usw.suwiki.domain.user.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.blacklistdomain.service.BlacklistDomainCRUDService;
import usw.suwiki.domain.blacklistdomain.service.BlacklistDomainService;
import usw.suwiki.domain.confirmationtoken.ConfirmationToken;
import usw.suwiki.domain.confirmationtoken.service.ConfirmationTokenCRUDService;
import usw.suwiki.domain.evaluation.domain.EvaluatePosts;
import usw.suwiki.domain.evaluation.service.EvaluatePostCRUDService;
import usw.suwiki.domain.exam.domain.ExamPosts;
import usw.suwiki.domain.exam.service.ExamPostCRUDService;
import usw.suwiki.domain.exam.service.ViewExamCRUDService;
import usw.suwiki.domain.favoritemajor.dto.FavoriteSaveDto;
import usw.suwiki.domain.favoritemajor.service.FavoriteMajorService;
import usw.suwiki.domain.postreport.service.ReportPostService;
import usw.suwiki.domain.refreshToken.RefreshToken;
import usw.suwiki.domain.refreshToken.service.RefreshTokenCRUDService;
import usw.suwiki.domain.restrictinguser.service.RestrictingUserCRUDService;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.controller.dto.UserRequestDto.EvaluateReportForm;
import usw.suwiki.domain.user.user.controller.dto.UserRequestDto.ExamReportForm;
import usw.suwiki.domain.user.user.controller.dto.UserResponseDto.LoadMyBlackListReasonResponseForm;
import usw.suwiki.domain.user.user.controller.dto.UserResponseDto.LoadMyRestrictedReasonResponseForm;
import usw.suwiki.domain.user.user.controller.dto.UserResponseDto.UserInformationResponseForm;
import usw.suwiki.domain.user.userIsolation.UserIsolation;
import usw.suwiki.domain.user.userIsolation.service.UserIsolationCRUDService;
import usw.suwiki.global.ResponseForm;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtAgent;
import usw.suwiki.global.util.emailBuild.BuildEmailAuthForm;
import usw.suwiki.global.util.emailBuild.BuildFindLoginIdForm;
import usw.suwiki.global.util.emailBuild.BuildFindPasswordForm;
import usw.suwiki.global.util.mailsender.EmailSender;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static usw.suwiki.domain.postreport.EvaluatePostReport.buildEvaluatePostReport;
import static usw.suwiki.domain.postreport.ExamPostReport.buildExamPostReport;
import static usw.suwiki.domain.user.user.controller.dto.UserResponseDto.UserInformationResponseForm.buildMyPageResponseForm;
import static usw.suwiki.global.exception.ExceptionType.*;
import static usw.suwiki.global.util.apiresponse.ApiResponseFactory.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserBusinessService {

    private static final String BASE_LINK = "https://api.suwiki.kr/user/verify-email/?token=";
    private static final String LOCAL_BASE_LINK = "http://localhost:8080/user/verify-email/?token=";
    private static final String MAIL_FORM = "@suwon.ac.kr";
    private final UserCRUDService userCRUDService;
    private final UserIsolationCRUDService userIsolationCRUDService;
    private final ConfirmationTokenCRUDService confirmationTokenCRUDService;
    private final EmailSender emailSender;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RefreshTokenCRUDService refreshTokenCRUDService;
    private final BuildEmailAuthForm buildEmailAuthForm;
    private final BuildFindLoginIdForm BuildFindLoginIdForm;
    private final BuildFindPasswordForm BuildFindPasswordForm;
    private final BlacklistDomainService blacklistDomainService;
    private final JwtAgent jwtAgent;
    private final FavoriteMajorService favoriteMajorService;
    private final ViewExamCRUDService viewExamCRUDService;
    private final EvaluatePostCRUDService evaluatePostCRUDService;
    private final ExamPostCRUDService examPostCRUDService;
    private final ReportPostService reportPostService;
    private final BlacklistDomainCRUDService blacklistDomainCRUDService;
    private final RestrictingUserCRUDService restrictingUserCRUDService;

    @Transactional(readOnly = true)
    public Map<String, Boolean> executeCheckId(String loginId) {
        if (userCRUDService.loadWrappedUserFromLoginId(loginId).isPresent() ||
                userIsolationCRUDService.loadWrappedUserFromLoginId(loginId).isPresent()) {
            return overlapTrueFlag();
        }
        return overlapFalseFlag();
    }

    @Transactional(readOnly = true)
    public Map<String, Boolean> executeCheckEmail(String email) {
        if (userCRUDService.loadWrappedUserFromEmail(email).isPresent() ||
                userIsolationCRUDService.loadWrappedUserFromEmail(email).isPresent()) {
            return overlapTrueFlag();
        }
        return overlapFalseFlag();
    }

    @Transactional
    public Map<String, Boolean> executeJoin(String loginId, String password, String email) {
        blacklistDomainService.isUserInBlackListThatRequestJoin(email);

        if (userCRUDService.loadWrappedUserFromLoginId(loginId).isPresent() ||
                userIsolationCRUDService.loadWrappedUserFromLoginId(loginId).isPresent() ||
                userCRUDService.loadWrappedUserFromEmail(email).isPresent() ||
                userIsolationCRUDService.loadWrappedUserFromEmail(email).isPresent())
            throw new AccountException(LOGIN_ID_OR_EMAIL_OVERLAP);

        if (!email.contains(MAIL_FORM)) throw new AccountException(IS_NOT_EMAIL_FORM);

        User user = User.makeUser(loginId, bCryptPasswordEncoder.encode(password), email);
        userCRUDService.saveUser(user);

        ConfirmationToken confirmationToken = ConfirmationToken.makeToken(user);
        confirmationTokenCRUDService.saveConfirmationToken(confirmationToken);

        emailSender.send(
                email,
                buildEmailAuthForm.buildEmail(LOCAL_BASE_LINK + confirmationToken.getToken())
        );
        return successFlag();
    }

    public Map<String, Boolean> executeFindId(String email) {
        Optional<User> requestUser = userCRUDService.loadWrappedUserFromEmail(email);
        Optional<UserIsolation> requestIsolationUser = userIsolationCRUDService.loadWrappedUserFromEmail(email);
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
        Optional<User> userByLoginId = userCRUDService.loadWrappedUserFromLoginId(loginId);
        Optional<User> userByEmail = userCRUDService.loadWrappedUserFromEmail(email);

        Optional<UserIsolation> isolationUserByLoginId =
                userIsolationCRUDService.loadWrappedUserFromLoginId(loginId);
        Optional<UserIsolation> isolationUserByEmail =
                userIsolationCRUDService.loadWrappedUserFromEmail(email);

        if (userByLoginId.equals(userByEmail) &&
                userByLoginId.isPresent() &&
                userByEmail.isPresent()) {
            User user = userByLoginId.get();
            emailSender.send(
                    email,
                    BuildFindPasswordForm.buildEmail(user.updateRandomPassword(bCryptPasswordEncoder))
            );
            return successFlag();
        } else if (isolationUserByLoginId.equals(isolationUserByEmail) &&
                isolationUserByLoginId.isPresent() && isolationUserByEmail.isPresent()
        ) {
            UserIsolation userIsolation = isolationUserByEmail.get();
            emailSender.send(
                    email,
                    BuildFindPasswordForm.buildEmail(userIsolation.updateRandomPassword(bCryptPasswordEncoder))
            );
            return successFlag();
        }
        throw new AccountException(USER_NOT_FOUND);
    }

    public Map<String, String> executeLogin(String loginId, String inputPassword) {
        if (userCRUDService.loadWrappedUserFromLoginId(loginId).isPresent() &&
                userIsolationCRUDService.loadWrappedUserFromLoginId(loginId).isEmpty()
        ) {
            User user = userCRUDService.loadUserFromLoginId(loginId);
            user.isUserEmailAuthed(confirmationTokenCRUDService.loadConfirmationTokenFromUserIdx(user.getId()));
            if (user.validatePassword(bCryptPasswordEncoder, inputPassword)) {
                user.updateLastLoginDate();
                return generateUserJWT(user);
            }
        } else if (userIsolationCRUDService.loadWrappedUserFromLoginId(loginId).isPresent() &&
                userCRUDService.loadWrappedUserFromLoginId(loginId).isEmpty()
        ) {
            UserIsolation userIsolation = userIsolationCRUDService.loadWrappedUserFromLoginId(loginId).get();
            if (userIsolation.validatePassword(bCryptPasswordEncoder, inputPassword)) {
                rollBackSoftDeletedForIsolation(userIsolation.getUserIdx());
                userIsolationCRUDService.deleteByLoginId(loginId);
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
        User user = userCRUDService.loadUserFromUserIdx(jwtAgent.getId(Authorization));
        if (!bCryptPasswordEncoder.matches(prePassword, user.getPassword())) {
            throw new AccountException(PASSWORD_ERROR);
        } else if (prePassword.equals(newPassword)) {
            throw new AccountException(PASSWORD_NOT_CHANGED);
        }
        user.updatePassword(bCryptPasswordEncoder, newPassword);
        return successFlag();
    }

    public UserInformationResponseForm executeLoadMyPage(String Authorization) {
        Long userIdx = jwtAgent.getId(Authorization);
        User user = userCRUDService.loadUserFromUserIdx(userIdx);
        return buildMyPageResponseForm(user);
    }

    public Map<String, String> executeJWTRefreshForWebClient(Cookie requestRefreshCookie) {
        String payload = requestRefreshCookie.getValue();
        RefreshToken refreshToken = refreshTokenCRUDService.loadRefreshTokenFromPayload(payload).get();
        User user = userCRUDService.loadUserFromUserIdx(refreshToken.getUserIdx());
        return refreshUserJWT(user, payload);
    }

    public Map<String, String> executeJWTRefreshForMobileClient(String Authorization) {
        RefreshToken refreshToken = refreshTokenCRUDService.loadRefreshTokenFromPayload(Authorization).get();
        User user = userCRUDService.loadUserFromUserIdx(refreshToken.getUserIdx());
        return refreshUserJWT(user, refreshToken.getPayload());
    }

    public Map<String, Boolean> executeQuit(String Authorization, String inputPassword) {
        jwtAgent.validateJwt(Authorization);
        User user = userCRUDService.loadUserFromUserIdx(jwtAgent.getId(Authorization));
        if (user.validatePassword(bCryptPasswordEncoder, inputPassword)) {
            throw new AccountException(PASSWORD_ERROR);
        }
        reportPostService.deleteFromUserIdx(user.getId());
        favoriteMajorService.deleteFromUserIdx(user.getId());
        viewExamCRUDService.deleteAllFromUserIdx(user.getId());
        examPostCRUDService.deleteFromUserIdx(user.getId());
        evaluatePostCRUDService.deleteFromUserIdx(user.getId());
        user.waitQuit();
        return successFlag();
    }

    public Map<String, Boolean> executeReportEvaluatePost(
            EvaluateReportForm evaluateReportForm,
            String Authorization
    ) {
        jwtAgent.validateJwt(Authorization);
        if (jwtAgent.getUserIsRestricted(Authorization)) throw new AccountException(USER_RESTRICTED);
        Long reportingUserIdx = jwtAgent.getId(Authorization);
        EvaluatePosts evaluatePost = evaluatePostCRUDService.loadEvaluatePostFromEvaluatePostIdx(
                evaluateReportForm.getEvaluateIdx());
        Long reportedUserIdx = evaluatePost.getUser().getId();

        reportPostService.saveEvaluatePostReport(
                buildEvaluatePostReport(
                        evaluateReportForm,
                        evaluatePost,
                        reportedUserIdx,
                        reportingUserIdx)
        );
        return successFlag();
    }

    public Map<String, Boolean> executeReportExamPost(
            ExamReportForm examReportForm,
            String Authorization
    ) {
        jwtAgent.validateJwt(Authorization);
        if (jwtAgent.getUserIsRestricted(Authorization)) throw new AccountException(USER_RESTRICTED);
        Long reportingUserIdx = jwtAgent.getId(Authorization);
        ExamPosts examPost = examPostCRUDService.loadExamPostFromExamPostIdx(
                examReportForm.getExamIdx());
        Long reportedUserIdx = examPost.getUser().getId();
        reportPostService.saveExamPostReport(
                buildExamPostReport(
                        examReportForm,
                        examPost,
                        reportedUserIdx,
                        reportingUserIdx)
        );
        return successFlag();
    }

    public List<LoadMyBlackListReasonResponseForm> executeLoadBlackListReason(String Authorization) {
        jwtAgent.validateJwt(Authorization);
        User requestUser = userCRUDService.loadUserFromUserIdx(jwtAgent.getId(Authorization));
        return blacklistDomainCRUDService.loadAllBlacklistLog(requestUser.getId());
    }

    public List<LoadMyRestrictedReasonResponseForm> executeLoadRestrictedReason(String Authorization) {
        jwtAgent.validateJwt(Authorization);
        User requestUser = userCRUDService.loadUserFromUserIdx(jwtAgent.getId(Authorization));
        return restrictingUserCRUDService.loadRestrictedLog(requestUser.getId());
    }

    public void executeFavoriteMajorSave(String Authorization, FavoriteSaveDto favoriteSaveDto) {
        jwtAgent.validateJwt(Authorization);
        if (jwtAgent.getUserIsRestricted(Authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }
        Long userIdx = jwtAgent.getId(Authorization);
        favoriteMajorService.save(favoriteSaveDto, userIdx);

    }

    public void executeFavoriteMajorDelete(String Authorization, String majorType) {
        jwtAgent.validateJwt(Authorization);
        if (jwtAgent.getUserIsRestricted(Authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }
        Long userIdx = jwtAgent.getId(Authorization);
        favoriteMajorService.delete(userIdx, majorType);
    }

    public ResponseForm executeFavoriteMajorLoad(String Authorization) {
        jwtAgent.validateJwt(Authorization);
        if (jwtAgent.getUserIsRestricted(Authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }
        Long userIdx = jwtAgent.getId(Authorization);
        List<String> list = favoriteMajorService.findMajorTypeByUser(userIdx);
        return new ResponseForm(list);
    }

    private void rollBackSoftDeletedForIsolation(Long userIdx) {
        User user = userCRUDService.loadUserFromUserIdx(userIdx);
        user.sleep();
    }

    private Map<String, String> generateUserJWT(User user) {
        return new HashMap<>() {{
            put("AccessToken", jwtAgent.createAccessToken(user));
            put("RefreshToken", jwtAgent.provideRefreshTokenInLogin(user));
        }};
    }

    private Map<String, String> refreshUserJWT(User user, String refreshTokenPayload) {
        return new HashMap<>() {{
            put("AccessToken", jwtAgent.createAccessToken(user));
            put("RefreshToken", jwtAgent.refreshTokenRefresh(refreshTokenPayload));
        }};
    }
}