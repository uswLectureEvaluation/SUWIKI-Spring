package usw.suwiki.domain.user.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.user.user.controller.dto.UserResponseDto.UserInformationResponseForm;
import usw.suwiki.domain.user.user.service.UserBusinessService;
import usw.suwiki.global.annotation.ApiLogger;

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

    //아이디 중복확인
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/check-loginId")
    public Map<String, Boolean> overlapId(
            @Valid @RequestBody CheckLoginIdForm checkLoginIdForm
    ) {
        return userBusinessService.executeCheckId(checkLoginIdForm.getLoginId());
    }

    //이메일 중복 확인
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/check-email")
    public Map<String, Boolean> overlapEmail(
            @Valid @RequestBody CheckEmailForm checkEmailForm
    ) {
        return userBusinessService.executeCheckEmail(checkEmailForm.getEmail());
    }

    //회원가입 버튼 클릭 시 -> 유저 저장, 인증 이메일 발송
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping
    public Map<String, Boolean> join(
            @Valid @RequestBody JoinForm joinForm
    ) {
        return userBusinessService.executeJoin(
                joinForm.getLoginId(),
                joinForm.getPassword(),
                joinForm.getEmail()
        );
    }

    //아이디 찾기 요청 시
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("inquiry-loginId")
    public Map<String, Boolean> findId(@Valid @RequestBody FindIdForm findIdForm) {
        return userBusinessService.executeFindId(findIdForm.getEmail());
    }

    //비밀번호 찾기 요청 시
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("inquiry-password")
    public Map<String, Boolean> findPw(
            @Valid @RequestBody FindPasswordForm findPasswordForm) {
        return userBusinessService.executeFindPw(
                findPasswordForm.getLoginId(),
                findPasswordForm.getEmail()
        );
    }

    //비밀번호 재설정 요청 시
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PatchMapping("password")
    public Map<String, Boolean> resetPw(
            @Valid @RequestBody EditMyPasswordForm editMyPasswordForm,
            @RequestHeader String Authorization) {
        return userBusinessService.executeEditPassword(
                Authorization,
                editMyPasswordForm.getPrePassword(),
                editMyPasswordForm.getNewPassword()
        );
    }

    // 안드, IOS 로그인 요청 시
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("mobile-login")
    public Map<String, String> mobileLogin(
            @Valid @RequestBody LoginForm loginForm) {
        return userBusinessService.executeLogin(
                loginForm.getLoginId(),
                loginForm.getPassword()
        );
    }

    // 프론트 로그인 요청 시 --> RefreshToken, AccessToken 쿠키로 셋팅
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("client-login")
    public Map<String, String> clientLogin(
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

        return new HashMap<>() {{
            put("AccessToken", tokenPair.get("AccessToken"));
        }};
    }

    // 프론트 로그아웃
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

    // 유저 정보
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @GetMapping
    public UserInformationResponseForm myPage(@Valid @RequestHeader String Authorization) {
        return userBusinessService.executeLoadMyPage(Authorization);
    }

    // 회원 탈퇴
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @DeleteMapping
    public Map<String, Boolean> userQuit(
            @Valid @RequestBody UserQuitForm userQuitForm,
            @Valid @RequestHeader String Authorization
    ) {
        return userBusinessService.executeQuit(Authorization, userQuitForm.getPassword());
    }
}



