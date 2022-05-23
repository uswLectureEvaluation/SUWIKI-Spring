package usw.suwiki.controller.userAdmin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.dto.userAdmin.UserAdminDto;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.jwt.JwtTokenResolver;
import usw.suwiki.jwt.JwtTokenValidator;
import usw.suwiki.service.lecture.LectureService;
import usw.suwiki.service.user.UserService;
import usw.suwiki.service.userAdmin.UserAdminService;

import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class UserAdminController {

    private final UserAdminService userAdminService;
    private final JwtTokenResolver jwtTokenResolver;
    private final JwtTokenValidator jwtTokenValidator;

    @PostMapping("/ban/evaluate-post")
    public HashMap<String, Boolean> banEvaluatePost(@Valid @RequestHeader String Authorization,
                                                    @Valid @RequestBody UserAdminDto.EvaluatePostBanForm evaluatePostBanForm) {

        //토큰 검증
        jwtTokenValidator.validateAccessToken(Authorization);

        //토큰으로 유저 권한 확인 -> ADMIN 이 아니면 에러
        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN")) throw new AccountException(ErrorType.USER_RESTRICTED);

        HashMap<String, Boolean> result = new HashMap<>();

        // 게시글 삭제
        userAdminService.banishEvaluatePost(evaluatePostBanForm);

        // 유저 블랙리스트 테이블로
        userAdminService.banUser(userAdminService.banishEvaluatePost(evaluatePostBanForm), evaluatePostBanForm.getBannedTime());

        result.put("Success", true);
        return result;
    }

    @PostMapping("/ban/exam-post")
    public HashMap<String, Boolean> blackList(@Valid @RequestHeader String Authorization,
                                              @Valid @RequestBody UserAdminDto.ExamPostBanForm examPostBanForm) {

        //토큰 검증
        jwtTokenValidator.validateAccessToken(Authorization);

        //토큰으로 유저 권한 확인 -> ADMIN 이 아니면 에러
        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN")) throw new AccountException(ErrorType.USER_RESTRICTED);

        HashMap<String, Boolean> result = new HashMap<>();

        // 게시글 삭제
        userAdminService.banishExamPost(examPostBanForm);

        // 유저 블랙리스트 테이블로
        userAdminService.banUser(userAdminService.banishExamPost(examPostBanForm), examPostBanForm.getBannedTime());

        result.put("Success", true);
        return result;
    }

    @GetMapping("/ban/list")
    public UserAdminDto.ViewAllBannedPost loadReportedPosts(@Valid @RequestHeader String Authorization) {

        UserAdminDto.ViewAllBannedPost result = new UserAdminDto.ViewAllBannedPost();

        //토큰 검증
        jwtTokenValidator.validateAccessToken(Authorization);

        //토큰으로 유저 권한 확인 -> ADMIN 이 아니면 에러
        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN")) throw new AccountException(ErrorType.USER_RESTRICTED);

        result.setEvaluatePostReports(userAdminService.getReportedEvaluateList());
        result.setExamPostReports(userAdminService.getReportedExamList());

        return result;
    }
}
