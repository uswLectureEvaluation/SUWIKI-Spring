package usw.suwiki.domain.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.blacklistdomain.BlackListService;
import usw.suwiki.domain.email.service.EmailAuthService;
import usw.suwiki.domain.favoritemajor.dto.FavoriteSaveDto;
import usw.suwiki.domain.favoritemajor.service.FavoriteMajorService;
import usw.suwiki.domain.refreshToken.repository.RefreshTokenRepository;
import usw.suwiki.domain.restrictinguser.service.RestrictingUserService;
import usw.suwiki.domain.user.dto.UserDto;
import usw.suwiki.domain.user.dto.UserDto.*;
import usw.suwiki.domain.user.dto.UserResponseDto;
import usw.suwiki.domain.user.dto.UserResponseDto.ViewMyBlackListReasonForm;
import usw.suwiki.domain.user.dto.UserResponseDto.ViewMyRestrictedReasonForm;
import usw.suwiki.domain.user.entity.User;
import usw.suwiki.domain.user.repository.UserRepository;
import usw.suwiki.domain.user.service.UserService;
import usw.suwiki.domain.user.service.quitrequestuser.QuitRequestUserService;
import usw.suwiki.domain.userIsolation.repository.UserIsolationRepository;
import usw.suwiki.domain.userIsolation.service.UserIsolationService;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.exception.errortype.AccountException;
import usw.suwiki.global.ToJsonArray;
import usw.suwiki.global.jwt.JwtTokenProvider;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.jwt.JwtTokenValidator;
import usw.suwiki.global.util.emailBuild.BuildEmailAuthSuccessFormService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static usw.suwiki.exception.ErrorType.USER_NOT_EXISTS;
import static usw.suwiki.exception.ErrorType.USER_RESTRICTED;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final EmailAuthService emailAuthService;
    private final BuildEmailAuthSuccessFormService buildEmailAuthSuccessFormService;
    private final RestrictingUserService restrictingUserService;
    private final UserIsolationService userIsolationService;
    private final QuitRequestUserService quitRequestUserService;
    private final BlackListService blackListService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenResolver jwtTokenResolver;
    private final RefreshTokenRepository refreshTokenRepository;
    private final FavoriteMajorService favoriteMajorService;
    private final UserRepository userRepository;
    private final UserIsolationRepository userIsolationRepository;

    //아이디 중복확인
    @PostMapping("check-id")
    public Map<String, Boolean> overlapId(@Valid @RequestBody CheckIdForm checkId) {
        Map<String, Boolean> overlapLoginId = new HashMap<>();

        if (userRepository.findByLoginId(checkId.getLoginId()).isPresent() ||
                userIsolationRepository.findByLoginId(checkId.getLoginId()).isPresent()) {
            overlapLoginId.put("overlap", true);
            return overlapLoginId;
        }

        overlapLoginId.put("overlap", false);
        return overlapLoginId;
    }

    //이메일 중복 확인
    @PostMapping("check-email")
    public Map<String, Boolean> overlapEmail(@Valid @RequestBody CheckEmailForm checkEmailForm) {
        Map<String, Boolean> overlapEmail = new HashMap<>();
        blackListService.isBlackList(checkEmailForm.getEmail());

        if (userRepository.findByEmail(checkEmailForm.getEmail()).isPresent() ||
                userIsolationRepository.findByEmail(checkEmailForm.getEmail()).isPresent()) {
            overlapEmail.put("overlap", true);
            return overlapEmail;
        }

        overlapEmail.put("overlap", false);
        return overlapEmail;
    }

    //회원가입 버튼 클릭 시 -> 유저 저장, 인증 이메일 발송
    @PostMapping("join")
    public Map<String, Boolean> join(@Valid @RequestBody JoinForm joinForm) {
        Map<String, Boolean> joinSuccess = new HashMap<>();
        blackListService.isBlackList(joinForm.getEmail());
        userService.join(joinForm);
        joinSuccess.put("success", true);
        return joinSuccess;
    }

    @GetMapping("verify-email")
    public String ConfirmEmail(@RequestParam("token") String token) {
        String result = buildEmailAuthSuccessFormService.buildEmail();
        emailAuthService.confirmToken(token);
        emailAuthService.mailAuthSuccess(token);
        return result;
    }

    //아이디 찾기 요청 시
    @PostMapping("find-id")
    public Map<String, Boolean> findId(@Valid @RequestBody FindIdForm findIdForm) {
        Map<String, Boolean> joinSuccess = new HashMap<>();
        if (!userService.sendEmailFindId(findIdForm)) throw new AccountException(USER_NOT_EXISTS);
        joinSuccess.put("success", true);
        return joinSuccess;
    }

    //비밀번호 찾기 요청 시
    @PostMapping("find-pw")
    public Map<String, Boolean> findPw(@Valid @RequestBody FindPasswordForm findPasswordForm) {
        Map<String, Boolean> findPwSuccess = new HashMap<>();
        if (!userService.sendEmailFindPassword(findPasswordForm)) throw new AccountException(USER_NOT_EXISTS);
        findPwSuccess.put("success", true);
        return findPwSuccess;
    }

    //비밀번호 재설정 요청 시
    @PostMapping("reset-pw")
    public Map<String, Boolean> resetPw(
            @Valid @RequestBody EditMyPasswordForm editMyPasswordForm,
            @RequestHeader String Authorization) {
        jwtTokenValidator.validateAccessToken(Authorization);
        userService.validatePasswordAtEditPassword(
                jwtTokenResolver.getLoginId(Authorization), editMyPasswordForm.getPrePassword());
        userService.compareNewPasswordVersusPrePassword(
                jwtTokenResolver.getLoginId(Authorization), editMyPasswordForm.getNewPassword());
        userService.editMyPassword(editMyPasswordForm, Authorization);
        Map<String, Boolean> findPwSuccess = new HashMap<>();
        findPwSuccess.put("success", true);
        return findPwSuccess;
    }

    // 안드, IOS 로그인 요청 시
    @PostMapping("login")
    public Map<String, String> mobileLogin(@Valid @RequestBody UserDto.LoginForm loginForm) {
        Map<String, String> token = new HashMap<>();
        if (userIsolationRepository.findByLoginId(loginForm.getLoginId()).isEmpty()) {
            User user = userService.loadUserFromLoginId(loginForm.getLoginId());
            userService.isUserEmailAuth(user.getId());
            if (userService.validatePasswordAtUserTable(loginForm.getLoginId(), loginForm.getPassword())) {
                String accessToken = jwtTokenProvider.createAccessToken(user);
                token.put("AccessToken", accessToken);
                String refreshToken = jwtTokenResolver.refreshTokenUpdateOrCreate(user);
                token.put("RefreshToken", refreshToken);
                userService.setLastLogin(user);
                return token;
            }
            throw new AccountException(ErrorType.PASSWORD_ERROR);
        }
        User user = userIsolationService.sleepingUserLogin(loginForm);
        String accessToken = jwtTokenProvider.createAccessToken(user);
        token.put("AccessToken", accessToken);
        String refreshToken = jwtTokenResolver.refreshTokenUpdateOrCreate(user);
        token.put("RefreshToken", refreshToken);
        userService.setLastLogin(user);
        return token;
    }

    // 프론트 로그인 요청 시 --> RefreshToken, AccessToken 쿠키로 셋팅
    @PostMapping("client-login")
    public ResponseEntity<Map<String, String>> clientLogin(@Valid @RequestBody UserDto.LoginForm loginForm, HttpServletResponse response) {
        Map<String, String> responseWrapper = new HashMap<>();
        if (userIsolationRepository.findByLoginId(loginForm.getLoginId()).isEmpty()) {
            User user = userService.loadUserFromLoginId(loginForm.getLoginId());
            userService.isUserEmailAuth(user.getId());
            if (userService.validatePasswordAtUserTable(loginForm.getLoginId(), loginForm.getPassword())) {
                String accessToken = jwtTokenProvider.createAccessToken(user);
                responseWrapper.put("AccessToken", accessToken);
                String refreshToken = jwtTokenResolver.refreshTokenUpdateOrCreate(user);
                Cookie refreshCookie = new Cookie("refreshToken", "");
                refreshCookie.setValue(refreshToken);
                refreshCookie.setMaxAge(14 * 24 * 60 * 60); // expires in 14 days
                refreshCookie.setSecure(true);
                refreshCookie.setHttpOnly(true);
                response.addCookie(refreshCookie);
                userService.setLastLogin(user);
                return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
            }
            throw new AccountException(ErrorType.PASSWORD_ERROR);
        } else if (userIsolationRepository.findByLoginId(loginForm.getLoginId()).isPresent()) {
            User user = userIsolationService.sleepingUserLogin(loginForm);
            String accessToken = jwtTokenProvider.createAccessToken(user);
            responseWrapper.put("AccessToken", accessToken);
            String refreshToken = jwtTokenResolver.refreshTokenUpdateOrCreate(user);
            Cookie refreshCookie = new Cookie("refreshToken", "");
            refreshCookie.setValue(refreshToken);
            refreshCookie.setMaxAge(14 * 24 * 60 * 60); // expires in 7 days
            refreshCookie.setSecure(true);
            refreshCookie.setHttpOnly(true);
            response.addCookie(refreshCookie);
            userService.setLastLogin(user);
            return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
        }
        throw new AccountException(ErrorType.PASSWORD_ERROR);
    }

    // 프론트 로그아웃
    @PostMapping("client-logout")
    public ResponseEntity<Map<String, Boolean>> clientLogout(HttpServletResponse response) {
        Cookie refreshCookie = new Cookie("refreshToken", "");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
        Map<String, Boolean> result = new HashMap<>();
        result.put("Success", true);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/my-page")
    public UserResponseDto.MyPageResponse myPage(@Valid @RequestHeader String Authorization) {
        jwtTokenValidator.validateAccessToken(Authorization);
        Long userIdx = jwtTokenResolver.getId(Authorization);
        User user = userService.loadUserFromUserIdx(userIdx);
        return UserResponseDto.MyPageResponse.builder()
                .loginId(user.getLoginId())
                .email(user.getEmail())
                .point(user.getPoint())
                .writtenEvaluation(user.getWrittenEvaluation())
                .writtenExam(user.getWrittenExam())
                .viewExam(user.getViewExamCount())
                .build();
    }

    @PostMapping("/client-refresh")
    public ResponseEntity<Map<String, String>> clientTokenRefresh(@CookieValue(value = "refreshToken") Cookie reqRefreshCookie, HttpServletResponse response) {
        Map<String, String> responseWrapper = new HashMap<>();
        String refreshToken = reqRefreshCookie.getValue();
        if (refreshTokenRepository.findByPayload(refreshToken).isEmpty())
            throw new AccountException(USER_RESTRICTED);
        Long userIdx = refreshTokenRepository.findByPayload(refreshToken).get().getUserIdx();
        User user = userService.loadUserFromUserIdx(userIdx);
        String accessToken = jwtTokenProvider.createAccessToken(user);
        responseWrapper.put("AccessToken", accessToken);
        String newRefreshToken = jwtTokenResolver.refreshTokenUpdateOrCreate(user);
        Cookie refreshCookie = new Cookie("refreshToken", "");
        refreshCookie.setValue(newRefreshToken);
        refreshCookie.setMaxAge(14 * 24 * 60 * 60); // expires in 7 days
        refreshCookie.setSecure(true);
        refreshCookie.setHttpOnly(true);
        response.addCookie(refreshCookie);
        userService.setLastLogin(user);
        return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public Map<String, String> tokenRefresh(@Valid @RequestHeader String Authorization) {
        Map<String, String> token = new HashMap<>();
        if (refreshTokenRepository.findByPayload(Authorization).isEmpty())
            throw new AccountException(USER_RESTRICTED);
        Long userIdx = refreshTokenRepository.findByPayload(Authorization).get().getUserIdx();
        User user = userService.loadUserFromUserIdx(userIdx);
        String newRefreshToken = jwtTokenResolver.refreshTokenUpdateOrCreate(user);
        token.put("AccessToken", jwtTokenProvider.createAccessToken(user));
        token.put("RefreshToken", newRefreshToken);
        userService.setLastLogin(user);
        return token;
    }

    @PostMapping("quit")
    public Map<String, Boolean> userQuit(
            @Valid @RequestBody UserQuitForm userQuitForm, @Valid @RequestHeader String Authorization) {
        jwtTokenValidator.validateAccessToken(Authorization);
        Map<String, Boolean> result = new HashMap<>();
        if (!userService.validatePasswordAtUserTable(userQuitForm.getLoginId(), userQuitForm.getPassword()))
            throw new AccountException(USER_NOT_EXISTS);
        User theUserRequestedQuit = userService.loadUserFromLoginId(userQuitForm.getLoginId());
        quitRequestUserService.waitQuit(theUserRequestedQuit.getId());
        quitRequestUserService.requestQuitDateStamp(theUserRequestedQuit);
        result.put("success", true);
        return result;
    }

    // 시험정보 신고
    @PostMapping("/report/exam")
    public Map<String, Boolean> reportExam(
            @Valid @RequestBody ExamReportForm examReportForm, @Valid @RequestHeader String Authorization) {

        Map<String, Boolean> result = new HashMap<>();
        jwtTokenValidator.validateAccessToken(Authorization);
        if (jwtTokenResolver.getUserIsRestricted(Authorization)) throw new AccountException(USER_RESTRICTED);
        Long reportingUser = jwtTokenResolver.getId(Authorization);
        userService.reportExamPost(examReportForm, reportingUser);
        result.put("success", true);
        return result;
    }

    // 강의평가 신고
    @PostMapping("/report/evaluate")
    public Map<String, Boolean> reportEvaluate(
            @Valid @RequestBody EvaluateReportForm evaluateReportForm, @Valid @RequestHeader String Authorization) {

        Map<String, Boolean> result = new HashMap<>();
        jwtTokenValidator.validateAccessToken(Authorization);
        if (jwtTokenResolver.getUserIsRestricted(Authorization)) throw new AccountException(USER_RESTRICTED);
        Long reportingUser = jwtTokenResolver.getId(Authorization);
        userService.reportEvaluatePost(evaluateReportForm, reportingUser);
        result.put("success", true);
        return result;
    }

    @PostMapping("/favorite-major")
    public ResponseEntity<String> saveFavoriteMajor(@RequestHeader String Authorization, @RequestBody FavoriteSaveDto dto) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization))
                throw new AccountException(USER_RESTRICTED);
            Long userIdx = jwtTokenResolver.getId(Authorization);
            favoriteMajorService.save(dto, userIdx);
            return new ResponseEntity<String>("success", header, HttpStatus.valueOf(200));
        } else throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
    }

    @DeleteMapping("/favorite-major")
    public ResponseEntity<String> deleteFavoriteMajor(@RequestHeader String Authorization, @RequestParam String majorType) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization))
                throw new AccountException(USER_RESTRICTED);
            Long userIdx = jwtTokenResolver.getId(Authorization);
            favoriteMajorService.delete(userIdx, majorType);
            return new ResponseEntity<String>("success", header, HttpStatus.valueOf(200));
        } else throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
    }

    @GetMapping("/favorite-major")
    public ResponseEntity<ToJsonArray> findByLecture(@RequestHeader String Authorization) {
        HttpHeaders header = new HttpHeaders();
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization))
                throw new AccountException(USER_RESTRICTED);
            Long userIdx = jwtTokenResolver.getId(Authorization);
            List<String> list = favoriteMajorService.findMajorTypeByUser(userIdx);
            ToJsonArray data = new ToJsonArray(list);
            return new ResponseEntity<ToJsonArray>(data, header, HttpStatus.valueOf(200));
        } else throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
    }

    @GetMapping("/suki")
    public String thanksToSuki() {
        return "<center>\uD83D\uDE00 Thank You Suki! \uD83D\uDE00 <br><br> You gave to me a lot of knowledge <br><br>" +
                "He is my Tech-Mentor <br><br>" +
                "If you wanna contact him <br><br>" +
                "<a href = https://github.com/0xsuky> " +
                "<b>https://github.com/0xsuky<b>" +
                "</center>";
    }

    @GetMapping("/blacklist-reason")
    public ResponseEntity<List<ViewMyBlackListReasonForm>> banReason(@Valid @RequestHeader String Authorization) {
        jwtTokenValidator.validateAccessToken(Authorization);
        User requestUser = userService.loadUserFromUserIdx(jwtTokenResolver.getId(Authorization));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(blackListService.getBlacklistLog(requestUser.getId()));
    }

    // 정지 사유 불러오기
    @GetMapping("/restricted-reason")
    public ResponseEntity<List<ViewMyRestrictedReasonForm>> restrictedReason(@Valid @RequestHeader String Authorization) {
        jwtTokenValidator.validateAccessToken(Authorization);
        User requestUser = userService.loadUserFromUserIdx(jwtTokenResolver.getId(Authorization));
        return ResponseEntity.
                status(HttpStatus.OK)
                .body(restrictingUserService.getRestrictedLog(requestUser.getId()));
    }
}


