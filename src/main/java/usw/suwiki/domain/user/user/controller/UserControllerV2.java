package usw.suwiki.domain.user.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.user.user.service.UserBusinessService;
import usw.suwiki.global.ResponseForm;
import usw.suwiki.global.annotation.ApiLogger;
import usw.suwiki.global.annotation.JWTVerify;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.domain.user.user.controller.dto.UserRequestDto.*;

@RestController
@RequestMapping("/v2/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserControllerV2 {

    private final UserBusinessService userBusinessService;

    @Operation(summary = "아이디 중복 확인")
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/loginId/check")
    public ResponseForm overlapId(
            @Valid @RequestBody CheckLoginIdForm checkLoginIdForm
    ) {
        return ResponseForm.success(userBusinessService.executeCheckId(checkLoginIdForm.loginId()));
    }

    @Operation(summary = "이메일 중복 확인")
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/email/check")
    public ResponseForm overlapEmail(
            @Valid @RequestBody CheckEmailForm checkEmailForm
    ) {
        return ResponseForm.success(userBusinessService.executeCheckEmail(checkEmailForm.email()));
    }

    @Operation(summary = "회원가입")
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping
    public ResponseForm join(
            @Valid @RequestBody JoinForm joinForm
    ) {
        return ResponseForm.success(userBusinessService.executeJoin(
                joinForm.loginId(),
                joinForm.password(),
                joinForm.email()
        ));
    }

    @Operation(summary = "아이디 찾기")
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("inquiry-loginId")
    public ResponseForm findId(@Valid @RequestBody FindIdForm findIdForm) {
        return ResponseForm.success(userBusinessService.executeFindId(findIdForm.email()));
    }

    @Operation(summary = "비밀번호 찾기")
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("inquiry-password")
    public ResponseForm findPw(
            @Valid @RequestBody FindPasswordForm findPasswordForm
    ) {
        return ResponseForm.success(userBusinessService.executeFindPw(
                findPasswordForm.loginId(),
                findPasswordForm.email())
        );
    }

    @Operation(summary = "비밀번호 재설정")
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PatchMapping("password")
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

    @Operation(summary = "Mobile Client 로그인")
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("mobile-login")
    public ResponseForm mobileLogin(
            @Valid @RequestBody LoginForm loginForm
    ) {
        return ResponseForm.success(userBusinessService.executeLogin(
                loginForm.loginId(),
                loginForm.password())
        );
    }

    @Operation(summary = "Web Client 로그인")
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("web-login")
    public ResponseForm webLogin(
            @Valid @RequestBody LoginForm loginForm,
            HttpServletResponse response
    ) {
        Map<String, String> tokenPair = userBusinessService.executeLogin(
                loginForm.loginId(),
                loginForm.password()
        );
        Cookie refreshCookie = new Cookie("refreshToken", tokenPair.get("RefreshToken"));
        refreshCookie.setMaxAge(270 * 24 * 60 * 60); // expires in 14 days
        refreshCookie.setSecure(true);
        refreshCookie.setHttpOnly(true);
        response.addCookie(refreshCookie);

        return ResponseForm.success(new HashMap<>() {{
            put("AccessToken", tokenPair.get("AccessToken"));
        }});
    }

    @JWTVerify
    @Operation(summary = "로그아웃")
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("client-logout")
    public ResponseForm clientLogout(HttpServletResponse response) {
        Cookie refreshCookie = new Cookie("refreshToken", "");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
        return ResponseForm.success(new HashMap<>() {{
            put("Success", true);
        }});
    }

    @JWTVerify
    @Operation(summary = "유저 기본 정보를 불러온다.")
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @GetMapping
    public ResponseForm myPage(@Valid @RequestHeader String Authorization) {
        return ResponseForm.success(userBusinessService.executeLoadMyPage(Authorization));
    }

    @JWTVerify
    @Operation(summary = "회원탈퇴")
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @DeleteMapping
    public ResponseForm userQuit(
            @Valid @RequestBody UserQuitForm userQuitForm,
            @Valid @RequestHeader String Authorization
    ) {
        return ResponseForm.success(userBusinessService.executeQuit(Authorization, userQuitForm.password()));
    }
}



