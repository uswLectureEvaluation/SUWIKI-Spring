package usw.suwiki.domain.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.blacklistdomain.BlackListService;
import usw.suwiki.domain.favoritemajor.dto.FavoriteSaveDto;
import usw.suwiki.domain.restrictinguser.service.RestrictingUserService;
import usw.suwiki.domain.user.dto.UserRequestDto.*;
import usw.suwiki.domain.user.dto.UserResponseDto.MyPageResponse;
import usw.suwiki.domain.user.dto.UserResponseDto.ViewMyBlackListReasonForm;
import usw.suwiki.domain.user.dto.UserResponseDto.ViewMyRestrictedReasonForm;
import usw.suwiki.domain.user.service.UserService;
import usw.suwiki.domain.user.service.usecase.*;
import usw.suwiki.global.ToJsonArray;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.jwt.JwtTokenValidator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserCheckIdUseCase userCheckIdUseCase;
    private final UserCheckEmailUseCase userCheckEmailUseCase;
    private final UserJoinUseCase userJoinUseCase;
    private final UserVerifyEmailUseCase userVerifyEmailUseCase;
    private final UserFindIdUseCase userFindIdUseCase;
    private final UserFindPasswordUseCase userFindPasswordUseCase;
    private final UserResetPasswordUseCase userResetPasswordUseCase;
    private final UserLoginUseCase userLoginUseCase;
    private final UserMyPageUseCase userMyPageUseCase;
    private final UserTokenRefreshUseCase userTokenRefreshUseCase;
    private final UserQuitUseCase userQuitUseCase;
    private final UserReportUseCase userReportUseCase;
    private final UserFavoriteMajorUseCase userFavoriteMajorUseCase;
    private final UserLoadRestrictAndBlackListReasonUseCase userLoadRestrictAndBlackListReasonUseCase;

    //아이디 중복확인
    @PostMapping("check-id")
    public ResponseEntity<Map<String, Boolean>> overlapId(@Valid @RequestBody CheckLoginIdForm checkLoginIdForm) {
        return ResponseEntity
                .ok()
                .body(userCheckIdUseCase.execute(checkLoginIdForm));
    }

    //이메일 중복 확인
    @PostMapping("check-email")
    public ResponseEntity<Map<String, Boolean>> overlapEmail(@Valid @RequestBody CheckEmailForm checkEmailForm) {
        return ResponseEntity
                .ok()
                .body(userCheckEmailUseCase.execute(checkEmailForm));
    }

    //회원가입 버튼 클릭 시 -> 유저 저장, 인증 이메일 발송
    @PostMapping("join")
    public ResponseEntity<Map<String, Boolean>> join(@Valid @RequestBody JoinForm joinForm) {
        return ResponseEntity
                .ok()
                .body(userJoinUseCase.execute(joinForm));
    }

    // 이메일 인증 링크를 눌렀을 때
    @GetMapping("verify-email")
    public ResponseEntity<String> confirmEmail(@RequestParam("token") String token) {
        return ResponseEntity
                .ok()
                .body(userVerifyEmailUseCase.execute(token));
    }

    //아이디 찾기 요청 시
    @PostMapping("find-id")
    public ResponseEntity<Map<String, Boolean>> findId(@Valid @RequestBody FindIdForm findIdForm) {
        return ResponseEntity
                .ok()
                .body(userFindIdUseCase.execute(findIdForm));
    }

    //비밀번호 찾기 요청 시
    @PostMapping("find-pw")
    public ResponseEntity<Map<String, Boolean>> findPw(@Valid @RequestBody FindPasswordForm findPasswordForm) {
        return ResponseEntity
                .ok()
                .body(userFindPasswordUseCase.execute(findPasswordForm));
    }

    //비밀번호 재설정 요청 시
    @PostMapping("reset-pw")
    public ResponseEntity<Map<String, Boolean>> resetPw(
            @Valid @RequestBody EditMyPasswordForm editMyPasswordForm,
            @RequestHeader String Authorization) {
        return ResponseEntity
                .ok()
                .body(userResetPasswordUseCase.execute(Authorization, editMyPasswordForm));
    }

    // 안드, IOS 로그인 요청 시
    @PostMapping("login")
    public ResponseEntity<Map<String, String>> mobileLogin(@Valid @RequestBody LoginForm loginForm) {
        return ResponseEntity
                .ok()
                .body(userLoginUseCase.execute(loginForm));
    }

    // 프론트 로그인 요청 시 --> RefreshToken, AccessToken 쿠키로 셋팅
    @PostMapping("client-login")
    public ResponseEntity<Map<String, String>> clientLogin(
            @Valid @RequestBody LoginForm loginForm, HttpServletResponse response) {
        Map<String, String> tokenPair = userLoginUseCase.execute(loginForm);
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

    @GetMapping("/my-page")
    public ResponseEntity<MyPageResponse> myPage(@Valid @RequestHeader String Authorization) {
        return ResponseEntity
                .ok()
                .body(userMyPageUseCase.execute(Authorization));
    }

    // Web 토큰 갱신
    @PostMapping("/client-refresh")
    public ResponseEntity<Map<String, String>> clientTokenRefresh(
            @CookieValue(value = "refreshToken") Cookie requestRefreshCookie, HttpServletResponse response) {
        Map<String, String> tokenPair = userTokenRefreshUseCase.executeForWebClient(requestRefreshCookie);
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
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> tokenRefresh(@Valid @RequestHeader String Authorization) {
        return ResponseEntity
                .ok()
                .body(userTokenRefreshUseCase.executeForMobileClient(Authorization));
    }

    // 회원 탈퇴
    @PostMapping("quit")
    public ResponseEntity<Map<String, Boolean>> userQuit(
            @Valid @RequestBody UserQuitForm userQuitForm, @Valid @RequestHeader String Authorization) {
        return ResponseEntity
                .ok()
                .body(userQuitUseCase.execute(userQuitForm, Authorization));
    }

    // 강의평가 신고
    @PostMapping("/report/evaluate")
    public ResponseEntity<Map<String, Boolean>> reportEvaluate(
            @Valid @RequestBody EvaluateReportForm evaluateReportForm, @Valid @RequestHeader String Authorization) {
        return ResponseEntity
                .ok()
                .body(userReportUseCase.executeForEvaluatePost(evaluateReportForm, Authorization));
    }

    // 시험정보 신고
    @PostMapping("/report/exam")
    public ResponseEntity<Map<String, Boolean>> reportExam(
            @Valid @RequestBody ExamReportForm examReportForm, @Valid @RequestHeader String Authorization) {
        return ResponseEntity
                .ok()
                .body(userReportUseCase.executeForExamPost(examReportForm, Authorization));
    }

    // 전공 즐겨찾기 등록하기
    @PostMapping("/favorite-major")
    public ResponseEntity<String> saveFavoriteMajor(
            @RequestHeader String Authorization, @RequestBody FavoriteSaveDto favoriteSaveDto) {
        userFavoriteMajorUseCase.executeSave(Authorization, favoriteSaveDto);
        return ResponseEntity
                .ok()
                .body("success");
    }

    // 전공 즐겨찾기 삭제하기
    @DeleteMapping("/favorite-major")
    public ResponseEntity<String> deleteFavoriteMajor(
            @RequestHeader String Authorization, @RequestParam String majorType) {
        userFavoriteMajorUseCase.executeDelete(Authorization, majorType);
        return ResponseEntity
                .ok()
                .body("success");
    }

    // 전공 즐겨찾기 불러오기
    @GetMapping("/favorite-major")
    public ResponseEntity<ToJsonArray> findByLecture(@RequestHeader String Authorization) {
        return ResponseEntity
                .ok()
                .body(userFavoriteMajorUseCase.executeLoad(Authorization));
    }

    // 땡큐 영수형
    @GetMapping("/suki")
    public String thanksToSuki() {
        return "<center>\uD83D\uDE00 Thank You Suki! \uD83D\uDE00 <br><br> You gave to me a lot of knowledge <br><br>" +
                "He is my Tech-Mentor <br><br>" +
                "If you wanna contact him <br><br>" +
                "<a href = https://github.com/0xsuky> " +
                "<b>https://github.com/0xsuky<b>" +
                "</center>";
    }

    // 정지 사유 불러오기
    @GetMapping("/restricted-reason")
    public ResponseEntity<List<ViewMyRestrictedReasonForm>> restrictedReason(@Valid @RequestHeader String Authorization) {
        return ResponseEntity
                .ok()
                .body(userLoadRestrictAndBlackListReasonUseCase.executeForRestrictedReason(Authorization));
    }

    // 블랙리스트 사유 불러오기
    @GetMapping("/blacklist-reason")
    public ResponseEntity<List<ViewMyBlackListReasonForm>> banReason(@Valid @RequestHeader String Authorization) {
        return ResponseEntity
                .ok()
                .body(userLoadRestrictAndBlackListReasonUseCase.executeForBlackListReason(Authorization));
    }
}


