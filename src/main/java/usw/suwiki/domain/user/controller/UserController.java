package usw.suwiki.domain.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import usw.suwiki.domain.favoritemajor.dto.FavoriteSaveDto;
import usw.suwiki.domain.user.dto.UserRequestDto.CheckEmailForm;
import usw.suwiki.domain.user.dto.UserRequestDto.CheckLoginIdForm;
import usw.suwiki.domain.user.dto.UserRequestDto.EditMyPasswordForm;
import usw.suwiki.domain.user.dto.UserRequestDto.EvaluateReportForm;
import usw.suwiki.domain.user.dto.UserRequestDto.ExamReportForm;
import usw.suwiki.domain.user.dto.UserRequestDto.FindIdForm;
import usw.suwiki.domain.user.dto.UserRequestDto.FindPasswordForm;
import usw.suwiki.domain.user.dto.UserRequestDto.JoinForm;
import usw.suwiki.domain.user.dto.UserRequestDto.LoginForm;
import usw.suwiki.domain.user.dto.UserRequestDto.UserQuitForm;
import usw.suwiki.domain.user.dto.UserResponseDto.LoadMyBlackListReasonForm;
import usw.suwiki.domain.user.dto.UserResponseDto.LoadMyRestrictedReasonForm;
import usw.suwiki.domain.user.dto.UserResponseDto.MyPageForm;
import usw.suwiki.domain.user.service.UserCheckEmailService;
import usw.suwiki.domain.user.service.UserCheckIdService;
import usw.suwiki.domain.user.service.UserFavoriteMajorService;
import usw.suwiki.domain.user.service.UserFindIdService;
import usw.suwiki.domain.user.service.UserFindPasswordService;
import usw.suwiki.domain.user.service.UserJoinService;
import usw.suwiki.domain.user.service.UserLoadRestrictAndBlackListReasonService;
import usw.suwiki.domain.user.service.UserLoginService;
import usw.suwiki.domain.user.service.UserMyPageService;
import usw.suwiki.domain.user.service.UserQuitService;
import usw.suwiki.domain.user.service.UserReportService;
import usw.suwiki.domain.user.service.UserService;
import usw.suwiki.domain.user.service.UserTokenRefreshService;
import usw.suwiki.domain.user.service.UserVerifyEmailService;
import usw.suwiki.global.ResponseForm;
import usw.suwiki.global.annotation.ApiLogger;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.jwt.JwtTokenValidator;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    private final UserService userService;
    private final UserCheckIdService userCheckIdService;
    private final UserCheckEmailService userCheckEmailService;
    private final UserJoinService userJoinService;
    private final UserVerifyEmailService userVerifyEmailService;
    private final UserFindIdService userFindIdService;
    private final UserFindPasswordService userFindPasswordService;
    private final UserLoginService userLoginService;
    private final UserMyPageService userMyPageService;
    private final UserTokenRefreshService userTokenRefreshService;
    private final UserQuitService userQuitService;
    private final UserReportService userReportService;
    private final UserFavoriteMajorService userFavoriteMajorService;
    private final UserLoadRestrictAndBlackListReasonService userLoadRestrictAndBlackListReasonService;

    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenResolver jwtTokenResolver;

    //아이디 중복확인
    @ApiLogger(option = "user")
    @PostMapping("check-id")
    public ResponseEntity<Map<String, Boolean>> overlapId(
        @Valid @RequestBody CheckLoginIdForm checkLoginIdForm) {
        return ResponseEntity
            .ok()
            .body(userCheckIdService.execute(checkLoginIdForm));
    }

    //이메일 중복 확인
    @ApiLogger(option = "user")
    @PostMapping("check-email")
    public ResponseEntity<Map<String, Boolean>> overlapEmail(
        @Valid @RequestBody CheckEmailForm checkEmailForm) {
        return ResponseEntity
            .ok()
            .body(userCheckEmailService.execute(checkEmailForm));
    }

    //회원가입 버튼 클릭 시 -> 유저 저장, 인증 이메일 발송
    @ApiLogger(option = "user")
    @PostMapping("join")
    public ResponseEntity<Map<String, Boolean>> join(@Valid @RequestBody JoinForm joinForm) {
        return ResponseEntity
            .ok()
            .body(userJoinService.execute(joinForm));
    }

    // 이메일 인증 링크를 눌렀을 때
    @ApiLogger(option = "user")
    @GetMapping("verify-email")
    public ResponseEntity<String> confirmEmail(@RequestParam("token") String token) {
        return ResponseEntity
            .ok()
            .body(userVerifyEmailService.execute(token));
    }

    //아이디 찾기 요청 시
    @ApiLogger(option = "user")
    @PostMapping("find-id")
    public ResponseEntity<Map<String, Boolean>> findId(@Valid @RequestBody FindIdForm findIdForm) {
        return ResponseEntity
            .ok()
            .body(userFindIdService.execute(findIdForm));
    }

    //비밀번호 찾기 요청 시
    @ApiLogger(option = "user")
    @PostMapping("find-pw")
    public ResponseEntity<Map<String, Boolean>> findPw(
        @Valid @RequestBody FindPasswordForm findPasswordForm) {
        return ResponseEntity
            .ok()
            .body(userFindPasswordService.execute(findPasswordForm));
    }

    //비밀번호 재설정 요청 시
    @ResponseStatus(HttpStatus.OK)
    @ApiLogger(option = "user")
    @PostMapping("reset-pw")
    public Map<String, Boolean> resetPw(
        @Valid @RequestBody EditMyPasswordForm editMyPasswordForm,
        @RequestHeader String Authorization) {
        return userService.executeEditPassword(
            userService.loadUserFromUserIdx(jwtTokenResolver.getId(Authorization)),
            editMyPasswordForm.getPrePassword(),
            editMyPasswordForm.getNewPassword());
    }

    // 안드, IOS 로그인 요청 시
    @ApiLogger(option = "user")
    @PostMapping("login")
    public ResponseEntity<Map<String, String>> mobileLogin(
        @Valid @RequestBody LoginForm loginForm) {
        return ResponseEntity
            .ok()
            .body(userLoginService.execute(loginForm));
    }

    // 프론트 로그인 요청 시 --> RefreshToken, AccessToken 쿠키로 셋팅
    @ApiLogger(option = "user")
    @PostMapping("client-login")
    public ResponseEntity<Map<String, String>> clientLogin(
        @Valid @RequestBody LoginForm loginForm, HttpServletResponse response) {
        Map<String, String> tokenPair = userLoginService.execute(loginForm);
        Cookie refreshCookie = new Cookie("refreshToken", tokenPair.get("RefreshToken"));
        refreshCookie.setMaxAge(14 * 24 * 60 * 60); // expires in 14 days
        refreshCookie.setSecure(true);
        refreshCookie.setHttpOnly(true);
        response.addCookie(refreshCookie);
        return ResponseEntity
            .ok()
            .body(new HashMap<>() {{
                put("AccessToken", tokenPair.get("AccessToken"));
            }});
    }

    // 프론트 로그아웃
    @ApiLogger(option = "user")
    @PostMapping("client-logout")
    public ResponseEntity<Map<String, Boolean>> clientLogout(HttpServletResponse response) {
        Cookie refreshCookie = new Cookie("refreshToken", "");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
        return ResponseEntity
            .ok()
            .body(new HashMap<>() {{
                put("Success", true);
            }});
    }

    @ApiLogger(option = "user")
    @GetMapping("/my-page")
    public ResponseEntity<MyPageForm> myPage(@Valid @RequestHeader String Authorization) {
        return ResponseEntity
            .ok()
            .body(userMyPageService.execute(Authorization));
    }

    // Web 토큰 갱신
    @ApiLogger(option = "user")
    @PostMapping("/client-refresh")
    public ResponseEntity<Map<String, String>> clientTokenRefresh(
        @CookieValue(value = "refreshToken") Cookie requestRefreshCookie,
        HttpServletResponse response) {
        Map<String, String> tokenPair = userTokenRefreshService.executeForWebClient(
            requestRefreshCookie);
        Cookie refreshCookie = new Cookie("refreshToken", tokenPair.get("RefreshToken"));
        refreshCookie.setMaxAge(14 * 24 * 60 * 60); // expires in 7 days
        refreshCookie.setSecure(true);
        refreshCookie.setHttpOnly(true);
        response.addCookie(refreshCookie);
        return ResponseEntity
            .ok()
            .body(new HashMap<>() {{
                put("AccessToken", tokenPair.get("AccessToken"));
            }});
    }

    // Mobile 토큰 갱신
    @ApiLogger(option = "user")
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> tokenRefresh(
        @Valid @RequestHeader String Authorization) {
        return ResponseEntity
            .ok()
            .body(userTokenRefreshService.executeForMobileClient(Authorization));
    }

    // 회원 탈퇴
    @ApiLogger(option = "user")
    @PostMapping("quit")
    public ResponseEntity<Map<String, Boolean>> userQuit(
        @Valid @RequestBody UserQuitForm userQuitForm, @Valid @RequestHeader String Authorization) {
        return ResponseEntity
            .ok()
            .body(userQuitService.execute(userQuitForm, Authorization));
    }

    // 강의평가 신고
    @ApiLogger(option = "user")
    @PostMapping("/report/evaluate")
    public ResponseEntity<Map<String, Boolean>> reportEvaluate(
        @Valid @RequestBody EvaluateReportForm evaluateReportForm,
        @Valid @RequestHeader String Authorization) {
        return ResponseEntity
            .ok()
            .body(userReportService.executeForEvaluatePost(evaluateReportForm, Authorization));
    }

    // 시험정보 신고
    @ApiLogger(option = "user")
    @PostMapping("/report/exam")
    public ResponseEntity<Map<String, Boolean>> reportExam(
        @Valid @RequestBody ExamReportForm examReportForm,
        @Valid @RequestHeader String Authorization) {
        return ResponseEntity
            .ok()
            .body(userReportService.executeForExamPost(examReportForm, Authorization));
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
    public ResponseEntity<List<LoadMyRestrictedReasonForm>> restrictedReason(
        @Valid @RequestHeader String Authorization) {
        return ResponseEntity
            .ok()
            .body(userLoadRestrictAndBlackListReasonService.executeForRestrictedReason(
                Authorization));
    }

    // 블랙리스트 사유 불러오기
    @ApiLogger(option = "user")
    @GetMapping("/blacklist-reason")
    public ResponseEntity<List<LoadMyBlackListReasonForm>> banReason(
        @Valid @RequestHeader String Authorization) {
        return ResponseEntity
            .ok()
            .body(
                userLoadRestrictAndBlackListReasonService.executeForBlackListReason(Authorization));
    }
}


