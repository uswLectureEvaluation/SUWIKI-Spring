package usw.suwiki.domain.user.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.confirmationtoken.service.ConfirmationTokenService;
import usw.suwiki.domain.favoritemajor.dto.FavoriteSaveDto;
import usw.suwiki.domain.user.user.dto.UserResponseDto.LoadMyBlackListReasonResponseForm;
import usw.suwiki.domain.user.user.dto.UserResponseDto.LoadMyRestrictedReasonResponseForm;
import usw.suwiki.domain.user.user.dto.UserResponseDto.MyPageResponseForm;
import usw.suwiki.domain.user.user.service.UserFavoriteMajorService;
import usw.suwiki.domain.user.user.service.UserLoadRestrictAndBlackListReasonService;
import usw.suwiki.domain.user.user.service.UserReportService;
import usw.suwiki.domain.user.user.service.UserService;
import usw.suwiki.global.ResponseForm;
import usw.suwiki.global.annotation.ApiLogger;
import usw.suwiki.global.jwt.JwtResolver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.domain.user.user.dto.UserRequestDto.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;
    private final UserReportService userReportService;
    private final UserFavoriteMajorService userFavoriteMajorService;
    private final UserLoadRestrictAndBlackListReasonService userLoadRestrictAndBlackListReasonService;
    private final JwtResolver jwtResolver;

    //아이디 중복확인
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("check-id")
    public Map<String, Boolean> overlapId(
            @Valid @RequestBody CheckLoginIdForm checkLoginIdForm) {
        return userService.executeCheckId(checkLoginIdForm.getLoginId());
    }

    //이메일 중복 확인
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("check-email")
    public Map<String, Boolean> overlapEmail(
            @Valid @RequestBody CheckEmailForm checkEmailForm) {
        return userService.executeCheckEmail(checkEmailForm.getEmail());
    }

    //회원가입 버튼 클릭 시 -> 유저 저장, 인증 이메일 발송
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("join")
    public Map<String, Boolean> join(
            @Valid @RequestBody JoinForm joinForm
    ) {
        return userService.executeJoin(
                joinForm.getLoginId(),
                joinForm.getPassword(),
                joinForm.getEmail()
        );
    }

    // 이메일 인증 링크를 눌렀을 때
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @GetMapping("verify-email")
    public String confirmEmail(@RequestParam("token") String token) {
        return confirmationTokenService.confirmToken(token);
    }

    //아이디 찾기 요청 시
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("find-id")
    public Map<String, Boolean> findId(@Valid @RequestBody FindIdForm findIdForm) {
        return userService.executeFindId(findIdForm.getEmail());
    }

    //비밀번호 찾기 요청 시
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("find-pw")
    public Map<String, Boolean> findPw(
            @Valid @RequestBody FindPasswordForm findPasswordForm) {
        return userService.executeFindPw(
                findPasswordForm.getLoginId(),
                findPasswordForm.getEmail()
        );
    }

    //비밀번호 재설정 요청 시
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("reset-pw")
    public Map<String, Boolean> resetPw(
            @Valid @RequestBody EditMyPasswordForm editMyPasswordForm,
            @RequestHeader String Authorization) {
        return userService.executeEditPassword(
                userService.loadUserFromUserIdx(jwtResolver.getId(Authorization)),
                editMyPasswordForm.getPrePassword(),
                editMyPasswordForm.getNewPassword());
    }

    // 안드, IOS 로그인 요청 시
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("login")
    public Map<String, String> mobileLogin(
            @Valid @RequestBody LoginForm loginForm) {
        return userService.executeLogin(
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
        Map<String, String> tokenPair = userService.executeLogin(
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

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @GetMapping("/my-page")
    public MyPageResponseForm myPage(@Valid @RequestHeader String Authorization) {
        return userService.executeLoadMyPage(Authorization);
    }

    // Web 토큰 갱신
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/client-refresh")
    public Map<String, String> clientTokenRefresh(
            @CookieValue(value = "refreshToken") Cookie requestRefreshCookie,
            HttpServletResponse response
    ) {
        Map<String, String> tokenPair = userService.executeJWTRefreshForWebClient(
                requestRefreshCookie
        );
        Cookie refreshCookie = new Cookie("refreshToken", tokenPair.get("RefreshToken"));
        refreshCookie.setMaxAge(14 * 24 * 60 * 60); // expires in 7 days
        refreshCookie.setSecure(true);
        refreshCookie.setHttpOnly(true);
        response.addCookie(refreshCookie);
        return new HashMap<>() {{
            put("AccessToken", tokenPair.get("AccessToken"));
        }};
    }

    // Mobile 토큰 갱신
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/refresh")
    public Map<String, String> tokenRefresh(
            @Valid @RequestHeader String Authorization
    ) {
        return userService.executeJWTRefreshForMobileClient(Authorization);
    }

    // 회원 탈퇴
    @ResponseStatus
    @ApiLogger(option = "user")
    @PostMapping("quit")
    public Map<String, Boolean> userQuit(
            @Valid @RequestBody UserQuitForm userQuitForm,
            @Valid @RequestHeader String Authorization
    ) {
        return userService.executeQuit(Authorization, userQuitForm.getPassword());
    }

    // 강의평가 신고
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/report/evaluate")
    public Map<String, Boolean> reportEvaluate(
            @Valid @RequestBody EvaluateReportForm evaluateReportForm,
            @Valid @RequestHeader String Authorization
    ) {
        return userReportService.executeForEvaluatePost(evaluateReportForm, Authorization);
    }

    // 시험정보 신고
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("/report/exam")
    public Map<String, Boolean> reportExam(
            @Valid @RequestBody ExamReportForm examReportForm,
            @Valid @RequestHeader String Authorization) {
        return userReportService.executeForExamPost(examReportForm, Authorization);
    }

    // 전공 즐겨찾기 등록하기
    @ApiLogger(option = "user")
    @PostMapping("/favorite-major")
    public ResponseEntity<String> saveFavoriteMajor(
            @RequestHeader String Authorization, @RequestBody FavoriteSaveDto favoriteSaveDto) {
        userFavoriteMajorService.executeSave(Authorization, favoriteSaveDto);
        return ResponseEntity
                .ok()
                .body("success");
    }

    // 전공 즐겨찾기 삭제하기
    @ApiLogger(option = "user")
    @DeleteMapping("/favorite-major")
    public ResponseEntity<String> deleteFavoriteMajor(
            @RequestHeader String Authorization, @RequestParam String majorType) {
        userFavoriteMajorService.executeDelete(Authorization, majorType);
        return ResponseEntity
                .ok()
                .body("success");
    }

    // 전공 즐겨찾기 불러오기
    @ApiLogger(option = "user")
    @GetMapping("/favorite-major")
    public ResponseEntity<ResponseForm> findByLecture(@RequestHeader String Authorization) {
        return ResponseEntity
                .ok()
                .body(userFavoriteMajorService.executeLoad(Authorization));
    }

    // 땡큐 영수형
    @ApiLogger(option = "user")
    @GetMapping("/suki")
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

    // 정지 사유 불러오기
    @ApiLogger(option = "user")
    @GetMapping("/restricted-reason")
    public ResponseEntity<List<LoadMyRestrictedReasonResponseForm>> restrictedReason(
            @Valid @RequestHeader String Authorization) {
        return ResponseEntity
                .ok()
                .body(userLoadRestrictAndBlackListReasonService.executeForRestrictedReason(
                        Authorization));
    }

    // 블랙리스트 사유 불러오기
    @ApiLogger(option = "user")
    @GetMapping("/blacklist-reason")
    public ResponseEntity<List<LoadMyBlackListReasonResponseForm>> banReason(
            @Valid @RequestHeader String Authorization) {
        return ResponseEntity
                .ok()
                .body(
                        userLoadRestrictAndBlackListReasonService.executeForBlackListReason(Authorization));
    }
}


