package usw.suwiki.domain.user;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import usw.suwiki.domain.blacklistDomain.BlackListService;
import usw.suwiki.domain.user.quitRequestUser.QuitRequestUserService;
import usw.suwiki.domain.user.restrictingUser.RestrictingUserService;
import usw.suwiki.domain.user.sleepingUser.SleepingUserService;
import usw.suwiki.domain.userIsolation.UserIsolationRepository;
import usw.suwiki.global.ToJsonArray;
import usw.suwiki.domain.favorite_major.FavoriteSaveDto;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.global.jwt.JwtTokenProvider;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.jwt.JwtTokenValidator;
import usw.suwiki.domain.refreshToken.RefreshTokenRepository;
import usw.suwiki.domain.email.EmailAuthService;
import usw.suwiki.domain.emailBuild.BuildEmailAuthSuccessFormService;
import usw.suwiki.domain.favorite_major.FavoriteMajorService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    //User 관련 서비스
    private final UserService userService;
    private final EmailAuthService emailAuthService;
    private final BuildEmailAuthSuccessFormService buildEmailAuthSuccessFormService;
    private final RestrictingUserService restrictingUserService;

    // 휴면 계정 관련 서비스
    private final SleepingUserService sleepingUserService;

    // 회원탈퇴 요청 계정 관련 서비스
    private final QuitRequestUserService quitRequestUserService;

    // 블랙리스트 관련
    private final BlackListService blackListService;

    //JWT
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenResolver jwtTokenResolver;
    private final RefreshTokenRepository refreshTokenRepository;

    //학과 즐겨찾기 관련 서비스
    private final FavoriteMajorService favoriteMajorService;

    private final UserRepository userRepository;
    private final UserIsolationRepository userIsolationRepository;

    //아이디 중복확인
    @PostMapping("check-id")
    public HashMap<String, Boolean> overlapId(@Valid @RequestBody UserDto.CheckIdForm checkId) {

        //반환객체 생성
        HashMap<String, Boolean> overlapLoginId = new HashMap<>();

        //아이디가 이미 존재하면
        if (userRepository.findByLoginId(checkId.getLoginId()).isPresent() ||
                userIsolationRepository.findByLoginId(checkId.getLoginId()).isPresent()) {
            overlapLoginId.put("overlap", true);
            return overlapLoginId;
        }

        //아이디가 존재하지 않으면
        overlapLoginId.put("overlap", false);
        return overlapLoginId;
    }

    //이메일 중복 확인
    @PostMapping("check-email")
    public HashMap<String, Boolean> overlapEmail(@Valid @RequestBody UserDto.CheckEmailForm checkEmailForm) {

        //반환객체 생성
        HashMap<String, Boolean> overlapEmail = new HashMap<>();

        // 블랙리스트 유저면 에러 터뜨리기
        blackListService.isBlackList(checkEmailForm.getEmail());

        //이메일이 이미 존재하면
        if (userRepository.findByEmail(checkEmailForm.getEmail()).isPresent() ||
                userIsolationRepository.findByEmail(checkEmailForm.getEmail()).isPresent()) {
            
            overlapEmail.put("overlap", true);
            return overlapEmail;
        }

        //이메일이 존재하지 않으면
        overlapEmail.put("overlap", false);
        return overlapEmail;
    }

    //회원가입 버튼 클릭 시 -> 유저 저장, 인증 이메일 발송
    @PostMapping("join")
    public HashMap<String, Boolean> join(@Valid @RequestBody UserDto.JoinForm joinForm) {

        //반환객체 생성
        HashMap<String, Boolean> joinSuccess = new HashMap<>();

        //블랙리스트 테이블에 존재하는 유저면 에러 터뜨리기
        blackListService.isBlackList(joinForm.getEmail());

        //회원가입 비즈니스 로직 호출
        userService.join(joinForm);

        joinSuccess.put("success", true);
        return joinSuccess;
    }

    //이메일 인증 링크 클릭 시
    @GetMapping("verify-email")
    public String ConfirmEmail(@RequestParam("token") String token) {

        String result = buildEmailAuthSuccessFormService.buildEmail();

        emailAuthService.confirmToken(token); //토큰 검증 --Error Code = 400

        emailAuthService.mailAuthSuccess(token); // Restricted 해제 및 권한 부여

        return result;
    }

    //아이디 찾기 요청 시
    @PostMapping("find-id")
    public HashMap<String, Boolean> findId(@Valid @RequestBody UserDto.FindIdForm findIdForm) {

        //반환객체 생성
        HashMap<String, Boolean> joinSuccess = new HashMap<>();

        //아이디 찾기 요청 실패 시
        if (!userService.sendEmailFindId(findIdForm)) throw new AccountException(ErrorType.USER_NOT_EXISTS);

        joinSuccess.put("success", true);
        return joinSuccess;
    }

    //비밀번호 찾기 요청 시
    @PostMapping("find-pw")
    public HashMap<String, Boolean> findPw(@Valid @RequestBody UserDto.FindPasswordForm findPasswordForm) {

        //반환객체 생성
        HashMap<String, Boolean> findPwSuccess = new HashMap<>();

        //비밀번호 재설정 실패 시
        if (!userService.sendEmailFindPassword(findPasswordForm)) throw new AccountException(ErrorType.USER_NOT_EXISTS);

        findPwSuccess.put("success", true);
        return findPwSuccess;
    }

    //비밀번호 재설정 요청 시
    @PostMapping("reset-pw")
    public HashMap<String, Boolean> resetPw(@Valid @RequestBody UserDto.EditMyPasswordForm editMyPasswordForm, @RequestHeader String Authorization) {

        //토큰 검증
        jwtTokenValidator.validateAccessToken(Authorization);

        //로그인 아이디 및 현재 비밀번호 검증
        userService.validatePasswordAtEditPW(jwtTokenResolver.getLoginId(Authorization), editMyPasswordForm.getPrePassword());

        //토큰 검증 통과 시 반환 객체 생성
        HashMap<String, Boolean> findPwSuccess = new HashMap<>();

        //비밀번호 재설정 저장
        userService.editMyPassword(editMyPasswordForm, Authorization);

        findPwSuccess.put("success", true);
        return findPwSuccess;
    }

    // 안드, IOS 로그인 요청 시
    @PostMapping("login")
    public HashMap<String, String> mobileLogin(@Valid @RequestBody UserDto.LoginForm loginForm) {

        HashMap<String, String> token = new HashMap<>();

        if (userIsolationRepository.findByLoginId(loginForm.getLoginId()).isEmpty()) {

            User user = userService.loadUserFromLoginId(loginForm.getLoginId());

            //이메일 인증 받았는지 확인
            userService.isUserEmailAuth(loginForm.getLoginId());

            // 블랙리스트 유저인지 확인
//            blackListService.isBlackList(user.getEmail());

            //아이디 비밀번호 검증
            if (userService.validatePasswordAtUserTable(loginForm.getLoginId(), loginForm.getPassword())) {
                //액세스 토큰 생성
                String accessToken = jwtTokenProvider.createAccessToken(user);
                token.put("AccessToken", accessToken);

                // 리프레시 토큰 갱신 혹은 신규 생성 판단 및 생성
                String refreshToken = jwtTokenResolver.refreshTokenUpdateOrCreate(user);
                token.put("RefreshToken", refreshToken);

                //마지막 로그인 일자 스탬프
                userService.setLastLogin(user);

                return token;
            }

            throw new AccountException(ErrorType.PASSWORD_ERROR);
        }

        User user = sleepingUserService.sleepingUserLogin(loginForm);

        //액세스 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user);
        token.put("AccessToken", accessToken);

        // 리프레시 토큰 갱신 혹은 신규 생성 판단 및 생성
        String refreshToken = jwtTokenResolver.refreshTokenUpdateOrCreate(user);
        token.put("RefreshToken", refreshToken);

        //마지막 로그인 일자 스탬프
        userService.setLastLogin(user);

        return token;
    }

    // 프론트 로그인 요청 시 --> RefreshToken, AccessToken 쿠키로 셋팅
    @PostMapping("client-login")
    public ResponseEntity<?> clientLogin(@Valid @RequestBody UserDto.LoginForm loginForm, HttpServletResponse response) {

        HashMap<String, String> responseWrapper = new HashMap<>();

        // 휴면계정이 아닌 유저 -> 일반유저의 로그인로직
        if (userIsolationRepository.findByLoginId(loginForm.getLoginId()).isEmpty()) {

            User user = userService.loadUserFromLoginId(loginForm.getLoginId());

            //이메일 인증 받았는지 확인
            userService.isUserEmailAuth(loginForm.getLoginId());

            //아이디 비밀번호 검증
            if (userService.validatePasswordAtUserTable(loginForm.getLoginId(), loginForm.getPassword())) {
                
                // 액세스 토큰 생성
                String accessToken = jwtTokenProvider.createAccessToken(user);

                responseWrapper.put("AccessToken", accessToken);

                // 리프레시 토큰 갱신 혹은 신규 생성 판단 및 생성
                String refreshToken = jwtTokenResolver.refreshTokenUpdateOrCreate(user);

                Cookie refreshCookie = new Cookie("refreshToken", "");
                refreshCookie.setValue(refreshToken);
                refreshCookie.setMaxAge(14 * 24 * 60 * 60); // expires in 7 days
                refreshCookie.setSecure(true);
                refreshCookie.setHttpOnly(true);

                response.addCookie(refreshCookie);

                //마지막 로그인 일자 스탬프
                userService.setLastLogin(user);

                return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
            }
            throw new AccountException(ErrorType.PASSWORD_ERROR);
        }

        // 휴면계정일 경우의 로그인 로직
        else if (userIsolationRepository.findByLoginId(loginForm.getLoginId()).isPresent()) {
            User user = sleepingUserService.sleepingUserLogin(loginForm);

            // 액세스 토큰 생성
            String accessToken = jwtTokenProvider.createAccessToken(user);
            responseWrapper.put("AccessToken", accessToken);

            // 리프레시 토큰 갱신 혹은 신규 생성 판단 및 생성
            String refreshToken = jwtTokenResolver.refreshTokenUpdateOrCreate(user);

            Cookie refreshCookie = new Cookie("refreshToken", "");
            refreshCookie.setValue(refreshToken);
            refreshCookie.setMaxAge(14 * 24 * 60 * 60); // expires in 7 days
            refreshCookie.setSecure(true);
            refreshCookie.setHttpOnly(true);

            response.addCookie(refreshCookie);

            //마지막 로그인 일자 스탬프
            userService.setLastLogin(user);

            return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
        }
        throw new AccountException(ErrorType.PASSWORD_ERROR);
    }

    @GetMapping("/my-page")
    public UserResponseDto.MyPageResponse myPage(@Valid @RequestHeader String Authorization) {

        //AccessToken 만료 확인
        jwtTokenValidator.validateAccessToken(Authorization);

        //토큰에 담긴 userIdx 가져오기
        Long userIdx = jwtTokenResolver.getId(Authorization);

        //토큰에 담김 loginId를 통해 레포지토리에 접근하여 User 불러오기
        User user = userService.loadUserFromUserIdx(userIdx);

        //반환
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
    public ResponseEntity<?> clientTokenRefresh(@CookieValue(value = "refreshToken") Cookie reqRefreshCookie, HttpServletResponse response) {

        HashMap<String, String> responseWrapper = new HashMap<>();

        String refreshToken = reqRefreshCookie.getValue();

        // RefreshToken 유효기간 검증
        jwtTokenValidator.validateRefreshToken(refreshToken);

        // RefreshToken DB에 담겨있는지 확인(임의로 만든 토큰이 아닌지 확인하자.)
        if (refreshTokenRepository.findByPayload(refreshToken).isEmpty())
            throw new AccountException(ErrorType.USER_RESTRICTED);

        // 리프레시 토큰으로 유저 인덱스 뽑아오기
        Long userIdx = refreshTokenRepository.findByPayload(refreshToken).get().getUser().getId();

        // 해당 RefreshToken 으로 UserIndex 를 추출하여 객체 반환
        User user = userService.loadUserFromUserIdx(userIdx);

        // 액세스 토큰 생성 및 반환 객체에 담기
        String accessToken = jwtTokenProvider.createAccessToken(user);
        responseWrapper.put("AccessToken", accessToken);

        // 리프레시 토큰 갱신이 필요하면 갱신 해주기
        if (jwtTokenValidator.isNeedToUpdateRefreshToken(refreshToken)) {
            refreshToken = jwtTokenProvider.updateRefreshToken(user.getId());
        }
        
        // 리프레시 토큰 쿠키에 담기
        Cookie refreshCookie = new Cookie("refreshToken", "");
        refreshCookie.setValue(refreshToken);
        refreshCookie.setMaxAge(14 * 24 * 60 * 60); // expires in 7 days
        refreshCookie.setSecure(true);
        refreshCookie.setHttpOnly(true);

        response.addCookie(refreshCookie);

        //마지막 로그인 일자 스탬프
        userService.setLastLogin(user);

        return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public HashMap<String, String> tokenRefresh(@Valid @RequestHeader String Authorization) {

        //반환객체
        HashMap<String, String> token = new HashMap<>();

        //RefreshToken 유효기간 검증
        jwtTokenValidator.validateRefreshToken(Authorization);

        //RefreshToken DB에 담겨있는지 확인(임의로 만든 토큰이 아닌지 확인하자.)
        if (refreshTokenRepository.findByPayload(Authorization).isEmpty())
            throw new AccountException(ErrorType.USER_RESTRICTED);

        //리프레시 토큰으로 유저 인덱스 뽑아오기
        Long userIdx = refreshTokenRepository.findByPayload(Authorization).get().getUser().getId();

        //해당 RefreshToken 으로 UserIndex 를 추출하여 객체 반환
        User user = userService.loadUserFromUserIdx(userIdx);

        //리프레시 토큰 갱신이 필요하면
        if (jwtTokenValidator.isNeedToUpdateRefreshToken(Authorization)) {

            //RefreshToken 재생성
            String newRefreshToken = jwtTokenProvider.updateRefreshToken(user.getId());

            //반환 객체에 담기
            token.put("AccessToken", jwtTokenProvider.createAccessToken(user));
            token.put("RefreshToken", newRefreshToken);

            //마지막 로그인 일자 스탬프
            userService.setLastLogin(user);

            return token;
        }

        // 리프레시 토큰 갱신 필요없으면 액세스 토큰만 재생성
        token.put("AccessToken", jwtTokenProvider.createAccessToken(user));
        token.put("RefreshToken", Authorization);

        //마지막 로그인 일자 스탬프
        userService.setLastLogin(user);

        return token;
    }

    @PostMapping("quit")
    public HashMap<String, Boolean> userQuit(@Valid @RequestBody UserDto.UserQuitForm userQuitForm,
                                             @Valid @RequestHeader String Authorization) {

        //토큰 검증
        jwtTokenValidator.validateAccessToken(Authorization);

        //AccessToken 으로 요청 접근 권한이 있는지 확인
        if (jwtTokenResolver.getUserIsRestricted(Authorization)) throw new AccountException(ErrorType.USER_RESTRICTED);

        HashMap<String, Boolean> result = new HashMap<>();

        //아이디 비밀번호 검증 후 일치하지 않으면
        if (!userService.validatePasswordAtUserTable(
                userQuitForm.getLoginId(), userQuitForm.getPassword()))
            throw new AccountException(ErrorType.USER_NOT_EXISTS);

        //아이디 비밀번호 검증 후 일치하면
        //해당하는 유저 가져오기
        User theUserRequestedQuit = userService.loadUserFromLoginId(userQuitForm.getLoginId());

        // 해당 유저 아이디, 이메일 제외 모두 삭제
        // 작성한 강의평가, 시험정보도 모두 삭제
        quitRequestUserService.waitQuit(theUserRequestedQuit.getId());

        //회원탈퇴 요청 시각 스탬프
        quitRequestUserService.requestQuitDateStamp(theUserRequestedQuit);

        result.put("success", true);

        return result;
    }

    // 시험정보 신고
    @PostMapping("/report/exam")
    public HashMap<String, Boolean> reportExam(@Valid @RequestBody UserDto.ExamReportForm examReportForm,
                                               @Valid @RequestHeader String Authorization) {

        HashMap<String, Boolean> result = new HashMap<>();

        //토큰 검증
        jwtTokenValidator.validateAccessToken(Authorization);

        //AccessToken 으로 요청 접근 권한이 있는지 확인
        if (jwtTokenResolver.getUserIsRestricted(Authorization)) throw new AccountException(ErrorType.USER_RESTRICTED);

        Long reportingUser = jwtTokenResolver.getId(Authorization);

        //신고하기 비즈니스 로직 호출 --> 신고 테이블에 값 저장
        userService.reportExamPost(examReportForm, reportingUser);

        result.put("success", true);

        return result;
    }

    // 강의평가 신고
    @PostMapping("/report/evaluate")
    public HashMap<String, Boolean> reportEvaluate(@Valid @RequestBody UserDto.EvaluateReportForm evaluateReportForm,
                                                   @Valid @RequestHeader String Authorization) {

        HashMap<String, Boolean> result = new HashMap<>();

        //토큰 검증
        jwtTokenValidator.validateAccessToken(Authorization);

        //AccessToken 으로 요청 접근 권한이 있는지 확인
        if (jwtTokenResolver.getUserIsRestricted(Authorization)) throw new AccountException(ErrorType.USER_RESTRICTED);

        Long reportingUser = jwtTokenResolver.getId(Authorization);

        //신고하기 비즈니스 로직 호출 --> 신고 테이블에 값 저장
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
                throw new AccountException(ErrorType.USER_RESTRICTED);
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
                throw new AccountException(ErrorType.USER_RESTRICTED);
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
                throw new AccountException(ErrorType.USER_RESTRICTED);
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

    // 블랙리스트 사유 불러오기
    @GetMapping("/blacklist-reason")
    public ResponseEntity<List<UserResponseDto.ViewMyBlackListReasonForm>> banReason(@Valid @RequestHeader String Authorization) {

        //토큰 검증
        jwtTokenValidator.validateAccessToken(Authorization);

        // 유저 테이블의 유저 객체 불러오기
        User requestUser = userService.loadUserFromUserIdx(jwtTokenResolver.getId(Authorization));

        return ResponseEntity.status(HttpStatus.OK).body(blackListService.getBlacklistLog(requestUser.getId()));
    }

    // 정지 사유 불러오기
    @GetMapping("/restricted-reason")
    public ResponseEntity<List<UserResponseDto.ViewMyRestrictedReasonForm>> restrictedReason(@Valid @RequestHeader String Authorization) {

        //토큰 검증
        jwtTokenValidator.validateAccessToken(Authorization);

        // 유저 테이블의 유저 객체 불러오기
        User requestUser = userService.loadUserFromUserIdx(jwtTokenResolver.getId(Authorization));

        return ResponseEntity.status(HttpStatus.OK).body(restrictingUserService.getRestrictedLog(requestUser.getId()));
    }
}


