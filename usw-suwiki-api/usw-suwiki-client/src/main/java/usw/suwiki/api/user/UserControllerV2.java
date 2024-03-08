package usw.suwiki.api.user;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.core.annotation.JWTVerify;
import usw.suwiki.common.response.ResponseForm;
import usw.suwiki.domain.user.service.UserBusinessService;
import usw.suwiki.statistics.annotation.ApiLogger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.domain.user.dto.UserRequestDto.CheckEmailForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.CheckLoginIdForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.EditMyPasswordForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.FindIdForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.FindPasswordForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.JoinForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.LoginForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.UserQuitForm;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/v2/user")
@RequiredArgsConstructor
public class UserControllerV2 {
    private final UserBusinessService userBusinessService;

    @ApiLogger(option = "user")
    @Operation(summary = "아이디 중복 확인")
    @PostMapping("/loginId/check")
    @ResponseStatus(OK)
    public ResponseForm overlapId(@Valid @RequestBody CheckLoginIdForm checkLoginIdForm) {
        return ResponseForm.success(userBusinessService.executeCheckId(checkLoginIdForm.loginId()));
    }

    @ApiLogger(option = "user")
    @Operation(summary = "이메일 중복 확인")
    @PostMapping("/email/check")
    @ResponseStatus(OK)
    public ResponseForm overlapEmail(@Valid @RequestBody CheckEmailForm checkEmailForm) {
        return ResponseForm.success(userBusinessService.executeCheckEmail(checkEmailForm.email()));
    }

    @ApiLogger(option = "user")
    @Operation(summary = "회원가입")
    @PostMapping
    @ResponseStatus(OK)
    public ResponseForm join(@Valid @RequestBody JoinForm joinForm) {
        Map<?, ?> response = userBusinessService.executeJoin(joinForm.loginId(), joinForm.password(), joinForm.email());
        return ResponseForm.success(response);
    }

    @ApiLogger(option = "user")
    @Operation(summary = "아이디 찾기")
    @PostMapping("inquiry-loginId")
    @ResponseStatus(OK)
    public ResponseForm findId(@Valid @RequestBody FindIdForm findIdForm) {
        return ResponseForm.success(userBusinessService.executeFindId(findIdForm.email()));
    }

    @ApiLogger(option = "user")
    @Operation(summary = "비밀번호 찾기")
    @PostMapping("inquiry-password")
    @ResponseStatus(OK)
    public ResponseForm findPw(@Valid @RequestBody FindPasswordForm findPasswordForm) {
        return ResponseForm.success(userBusinessService.executeFindPw(
            findPasswordForm.loginId(),
            findPasswordForm.email())
        );
    }

    @ApiLogger(option = "user")
    @Operation(summary = "비밀번호 재설정")
    @PatchMapping("password")
    @ResponseStatus(OK)
    public ResponseForm resetPw(
        @Valid @RequestBody EditMyPasswordForm editMyPasswordForm,
        @RequestHeader String Authorization
    ) {
        return ResponseForm.success(userBusinessService.executeEditPassword(
            Authorization,
            editMyPasswordForm.prePassword(),
            editMyPasswordForm.newPassword())
        );
    }

    @ApiLogger(option = "user")
    @Operation(summary = "Mobile Client 로그인")
    @PostMapping("mobile-login")
    @ResponseStatus(OK)
    public ResponseForm mobileLogin(@Valid @RequestBody LoginForm loginForm) {
        return ResponseForm.success(userBusinessService.executeLogin(
            loginForm.loginId(),
            loginForm.password())
        );
    }

    @ApiLogger(option = "user")
    @Operation(summary = "Web Client 로그인")
    @PostMapping("web-login")
    @ResponseStatus(OK)
    public ResponseForm webLogin(
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

        return ResponseForm.success(new HashMap<>() {{
            put("AccessToken", tokenPair.get("AccessToken"));
        }});
    }

    @JWTVerify
    @ApiLogger(option = "user")
    @Operation(summary = "로그아웃")
    @PostMapping("client-logout")
    @ResponseStatus(OK)
    public ResponseForm clientLogout(HttpServletResponse response) {
        Cookie refreshCookie = new Cookie("refreshToken", "");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
        return ResponseForm.success(new HashMap<>() {{
            put("Success", true);
        }});
    }

    @JWTVerify
    @ApiLogger(option = "user")
    @Operation(summary = "유저 기본 정보를 불러온다.")
    @GetMapping
    @ResponseStatus(OK)
    public ResponseForm myPage(@Valid @RequestHeader String Authorization) {
        return ResponseForm.success(userBusinessService.executeLoadMyPage(Authorization));
    }

    @JWTVerify
    @ApiLogger(option = "user")
    @Operation(summary = "회원탈퇴")
    @DeleteMapping
    @ResponseStatus(OK)
    public ResponseForm userQuit(
        @Valid @RequestBody UserQuitForm userQuitForm,
        @Valid @RequestHeader String Authorization
    ) {
        return ResponseForm.success(userBusinessService.executeQuit(Authorization, userQuitForm.password()));
    }
}
