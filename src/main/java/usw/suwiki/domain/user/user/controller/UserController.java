package usw.suwiki.domain.user.user.controller;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.domain.user.user.controller.dto.UserRequestDto.CheckEmailForm;
import static usw.suwiki.domain.user.user.controller.dto.UserRequestDto.CheckLoginIdForm;
import static usw.suwiki.domain.user.user.controller.dto.UserRequestDto.EditMyPasswordForm;
import static usw.suwiki.domain.user.user.controller.dto.UserRequestDto.EvaluateReportForm;
import static usw.suwiki.domain.user.user.controller.dto.UserRequestDto.ExamReportForm;
import static usw.suwiki.domain.user.user.controller.dto.UserRequestDto.FindIdForm;
import static usw.suwiki.domain.user.user.controller.dto.UserRequestDto.FindPasswordForm;
import static usw.suwiki.domain.user.user.controller.dto.UserRequestDto.JoinForm;
import static usw.suwiki.domain.user.user.controller.dto.UserRequestDto.LoginForm;
import static usw.suwiki.domain.user.user.controller.dto.UserRequestDto.UserQuitForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.domain.confirmationtoken.service.ConfirmationTokenBusinessService;
import usw.suwiki.domain.favoritemajor.dto.FavoriteSaveDto;
import usw.suwiki.domain.user.user.controller.dto.UserResponseDto.LoadMyBlackListReasonResponseForm;
import usw.suwiki.domain.user.user.controller.dto.UserResponseDto.LoadMyRestrictedReasonResponseForm;
import usw.suwiki.domain.user.user.controller.dto.UserResponseDto.UserInformationResponseForm;
import usw.suwiki.domain.user.user.service.UserBusinessService;
import usw.suwiki.global.ResponseForm;
import usw.suwiki.global.annotation.ApiLogger;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.AccountException;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    private final UserBusinessService userBusinessService;
    private final ConfirmationTokenBusinessService confirmationTokenBusinessService;

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/check-id")
    public Map<String, Boolean> overlapId(@Valid @RequestBody CheckLoginIdForm checkLoginIdForm) {
        return userBusinessService.executeCheckId(checkLoginIdForm.loginId());
    }

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/check-email")
    public Map<String, Boolean> overlapEmail(@Valid @RequestBody CheckEmailForm checkEmailForm) {
        return userBusinessService.executeCheckEmail(checkEmailForm.email());
    }

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("join")
    public Map<String, Boolean> join(
        @Valid @RequestBody JoinForm joinForm
    ) {
        return userBusinessService.executeJoin(
            joinForm.loginId(),
            joinForm.password(),
            joinForm.email()
        );
    }

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @GetMapping(
        value = "verify-email",
        produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"
    )
    public String confirmEmail(@RequestParam("token") String token) {
        return confirmationTokenBusinessService.confirmToken(token);
    }

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("find-id")
    public Map<String, Boolean> findId(@Valid @RequestBody FindIdForm findIdForm) {
        return userBusinessService.executeFindId(findIdForm.email());
    }

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("find-pw")
    public Map<String, Boolean> findPw(@Valid @RequestBody FindPasswordForm findPasswordForm) {
        return userBusinessService.executeFindPw(
            findPasswordForm.loginId(),
            findPasswordForm.email()
        );
    }

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("reset-pw")
    public Map<String, Boolean> resetPw(
        @Valid @RequestBody EditMyPasswordForm editMyPasswordForm,
        @RequestHeader String Authorization
    ) {
        return userBusinessService.executeEditPassword(
            Authorization,
            editMyPasswordForm.prePassword(),
            editMyPasswordForm.newPassword());
    }

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("login")
    public Map<String, String> mobileLogin(
        @Valid @RequestBody LoginForm loginForm) {
        return userBusinessService.executeLogin(
            loginForm.loginId(),
            loginForm.password()
        );
    }

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("client-login")
    public Map<String, String> clientLogin(
        @Valid @RequestBody LoginForm loginForm,
        HttpServletResponse response
    ) {
        Map<String, String> tokenPair = userBusinessService.executeLogin(
            loginForm.loginId(),
            loginForm.password()
        );
        Cookie refreshCookie = new Cookie("refreshToken", tokenPair.get("RefreshToken"));
        refreshCookie.setMaxAge(270 * 24 * 60 * 60);
        refreshCookie.setSecure(true);
        refreshCookie.setHttpOnly(true);
        response.addCookie(refreshCookie);

        return new HashMap<>() {{
            put("AccessToken", tokenPair.get("AccessToken"));
        }};
    }

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("client-logout")
    public Map<String, Boolean> clientLogout(HttpServletResponse response) {
        Cookie refreshCookie = new Cookie("refreshToken", "");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
        return new HashMap<>() {{
            put("Success", true);
        }};
    }

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @GetMapping("/my-page")
    public UserInformationResponseForm myPage(@Valid @RequestHeader String Authorization) {
        return userBusinessService.executeLoadMyPage(Authorization);
    }

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/client-refresh")
    public Map<String, String> clientTokenRefresh(
        @CookieValue(value = "refreshToken") Cookie requestRefreshCookie,
        HttpServletResponse response
    ) {
        Map<String, String> tokenPair = userBusinessService.executeJWTRefreshForWebClient(
            requestRefreshCookie
        );
        Cookie refreshCookie = new Cookie("refreshToken", tokenPair.get("RefreshToken"));
        refreshCookie.setMaxAge(14 * 24 * 60 * 60);
        refreshCookie.setSecure(true);
        refreshCookie.setHttpOnly(true);
        response.addCookie(refreshCookie);
        return new HashMap<>() {{
            put("AccessToken", tokenPair.get("AccessToken"));
        }};
    }

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/refresh")
    public Map<String, String> tokenRefresh(
        @Valid @RequestHeader String Authorization
    ) {
        return userBusinessService.executeJWTRefreshForMobileClient(Authorization);
    }

    // 회원 탈퇴
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ApiLogger(option = "user")
    @PostMapping("quit")
    public Map<String, Boolean> userQuit(
        @Valid @RequestBody UserQuitForm userQuitForm,
        @Valid @RequestHeader String Authorization
    ) {
//        return userBusinessService.executeQuit(
//                Authorization,
//                userQuitForm.password()
//        );

        // TODO fix: 회원 탈퇴 오류에 의한 임시 처리. 유저 탈퇴 로직 fix 후 복구해야 합니다.
        throw new AccountException(ExceptionType.SERVER_ERROR);
    }

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/report/evaluate")
    public Map<String, Boolean> reportEvaluate(
        @Valid @RequestBody EvaluateReportForm evaluateReportForm,
        @Valid @RequestHeader String Authorization
    ) {
        return userBusinessService.executeReportEvaluatePost(evaluateReportForm, Authorization);
    }

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/report/exam")
    public Map<String, Boolean> reportExam(
        @Valid @RequestBody ExamReportForm examReportForm,
        @Valid @RequestHeader String Authorization) {
        return userBusinessService.executeReportExamPost(examReportForm, Authorization);
    }

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/favorite-major")
    public String saveFavoriteMajor(
        @RequestHeader String Authorization,
        @RequestBody FavoriteSaveDto favoriteSaveDto
    ) {
        userBusinessService.executeFavoriteMajorSave(Authorization, favoriteSaveDto);
        return "success";
    }

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @DeleteMapping("/favorite-major")
    public String deleteFavoriteMajor(
        @RequestHeader String Authorization,
        @RequestParam String majorType) {
        userBusinessService.executeFavoriteMajorDelete(Authorization, majorType);
        return "success";
    }

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @GetMapping("/favorite-major")
    public ResponseForm loadFavoriteMajor(@RequestHeader String Authorization) {
        return userBusinessService.executeFavoriteMajorLoad(Authorization);
    }

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @GetMapping(value = "/suki", produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
    public String thanksToSuki() {
        return
            "<center>\uD83D\uDE00 Thank You Suki! \uD83D\uDE00 <br><br> You gave to me a lot of knowledge <br><br>"
                +
                "He is my Tech-Mentor <br><br>" +
                "If you wanna contact him <br><br>" +
                "<a href = https://github.com/0xsuky> " +
                "<b>https://github.com/0xsuky<b>" +
                "</center>";
    }

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @GetMapping("/restricted-reason")
    public List<LoadMyRestrictedReasonResponseForm> loadRestrictedReason(
        @Valid @RequestHeader String Authorization
    ) {
        return userBusinessService.executeLoadRestrictedReason(Authorization);
    }

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @GetMapping("/blacklist-reason")
    public List<LoadMyBlackListReasonResponseForm> loadBlacklistReason(
        @Valid @RequestHeader String Authorization) {
        return userBusinessService.executeLoadBlackListReason(Authorization);
    }
}


