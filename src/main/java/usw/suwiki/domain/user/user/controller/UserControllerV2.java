package usw.suwiki.domain.user.user.controller;

import io.swagger.annotations.ApiOperation;
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

    @ApiOperation(
            value = "아이디 중복 확인",
            notes = "Request Body에 담긴 LoginId 중복 확인 수행"
    )
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/check-loginId")
    public ResponseForm overlapId(
            @Valid @RequestBody CheckLoginIdForm checkLoginIdForm
    ) {
        return ResponseForm.success(userBusinessService.executeCheckId(checkLoginIdForm.getLoginId()));
    }

    @ApiOperation(
            value = "이메일 중복 확인",
            notes = "Request Body에 담긴 Email 중복 확인 수행"
    )
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/check-email")
    public ResponseForm overlapEmail(
            @Valid @RequestBody CheckEmailForm checkEmailForm
    ) {
        return ResponseForm.success(userBusinessService.executeCheckEmail(checkEmailForm.getEmail()));
    }

    @ApiOperation(
            value = "회원가입",
            notes = "Request Body에 담긴 정보를 바탕으로 재학생 인증 메일 발송"
    )
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping
    public ResponseForm join(
            @Valid @RequestBody JoinForm joinForm
    ) {
        return ResponseForm.success(userBusinessService.executeJoin(
                joinForm.getLoginId(),
                joinForm.getPassword(),
                joinForm.getEmail()
        ));
    }

    @ApiOperation(
            value = "아이디 찾기",
            notes = "Request Body에 담긴 정보를 바탕으로 아이디 찾기 결과를 메일로 발송한다."
    )
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("inquiry-loginId")
    public ResponseForm findId(@Valid @RequestBody FindIdForm findIdForm) {
        return ResponseForm.success(userBusinessService.executeFindId(findIdForm.getEmail()));
    }

    @ApiOperation(
            value = "비밀번호 찾기",
            notes = "Request Body에 담긴 정보를 바탕으로 재생성된 비밀번호 찾기 결과를 메일로 발송한다."
    )
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("inquiry-password")
    public ResponseForm findPw(
            @Valid @RequestBody FindPasswordForm findPasswordForm
    ) {
        return ResponseForm.success(userBusinessService.executeFindPw(
                findPasswordForm.getLoginId(),
                findPasswordForm.getEmail())
        );
    }

    @ApiOperation(
            value = "비밀번호 재설정",
            notes = "Request Body에 담긴 정보를 바탕으로 비밀번호를 재설정한다."
    )
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PatchMapping("password")
    public ResponseForm resetPw(
            @Valid @RequestBody EditMyPasswordForm editMyPasswordForm,
            @RequestHeader String Authorization
    ) {
        return ResponseForm.success(userBusinessService.executeEditPassword(
                Authorization,
                editMyPasswordForm.getPrePassword(),
                editMyPasswordForm.getNewPassword())
        );
    }

    @ApiOperation(
            value = "Mobile Client 로그인",
            notes = "Request Body에 담긴 정보를 바탕으로 로그인 성공 시 토큰을 발급한다."
    )
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("mobile-login")
    public ResponseForm mobileLogin(
            @Valid @RequestBody LoginForm loginForm
    ) {
        return ResponseForm.success(userBusinessService.executeLogin(
                loginForm.getLoginId(),
                loginForm.getPassword())
        );
    }

    @ApiOperation(
            value = "Web Client 로그인",
            notes = "Request Body에 담긴 정보를 바탕으로 로그인 성공 시 쿠키에 토큰을 발급한다."
    )
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("web-login")
    public ResponseForm webLogin(
            @Valid @RequestBody LoginForm loginForm,
            HttpServletResponse response
    ) {
        Map<String, String> tokenPair = userBusinessService.executeLogin(
                loginForm.getLoginId(),
                loginForm.getPassword()
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
    @ApiOperation(
            value = "로그아웃",
            notes = "쿠키 무효화"
    )
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
    @ApiOperation(
            value = "유저 기본 정보를 불러온다.",
            notes = "토큰에 담긴 정보를 바탕으로 해당 유저의 기본 정보를 불러온다."
    )
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @GetMapping
    public ResponseForm myPage(@Valid @RequestHeader String Authorization) {
        return ResponseForm.success(userBusinessService.executeLoadMyPage(Authorization));
    }

    @JWTVerify
    @ApiOperation(
            value = "회원탈퇴",
            notes = "토큰에 담긴 내용과 Request Body를 검증하여 회원탈퇴 처리를 수행한다."
    )
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @DeleteMapping
    public ResponseForm userQuit(
            @Valid @RequestBody UserQuitForm userQuitForm,
            @Valid @RequestHeader String Authorization
    ) {
        return ResponseForm.success(userBusinessService.executeQuit(Authorization, userQuitForm.getPassword()));
    }
}



