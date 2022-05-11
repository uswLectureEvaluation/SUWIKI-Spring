package usw.suwiki.controller.userAdmin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.reportTarget.ReportTarget;
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
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class UserAdminController {

    private final UserService userService;
    private final UserAdminService userAdminService;
    private final JwtTokenResolver jwtTokenResolver;
    private final JwtTokenValidator jwtTokenValidator;
    private final LectureService lectureService;

    @PostMapping("ban")
    public HashMap<String, Boolean> blackList(@Valid @RequestHeader String Authorization, @Valid @RequestBody UserAdminDto.BannedTargetForm bannedTargetForm) {

        //토큰 검증
        jwtTokenValidator.validateAccessToken(Authorization);

        //토큰으로 유저 권한 확인 -> ADMIN 이 아니면 에러
        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN")) throw new AccountException(ErrorType.USER_RESTRICTED);

        HashMap<String, Boolean> result = new HashMap<>();

        //게시글 삭제
        userAdminService.banishPost(bannedTargetForm);
        
        //PostType 이 True 일때 강의평가 게시글 조치
        if (bannedTargetForm.getPostType()) { userAdminService.decreaseWrittenEvaluation(bannedTargetForm); }

        //PostType 이 false 일때 시험정보 게시글 조치
        else { userAdminService.decreaseWrittenExam(bannedTargetForm); }

        //유저 블랙리스트 테이블로
        userAdminService.banUser(bannedTargetForm);

        result.put("Success", true);
        return result;
    }

    @GetMapping("ban")
    public List<ReportTarget> loadReportedPosts(@Valid @RequestHeader String Authorization) {

        HttpHeaders header = new HttpHeaders();

        //토큰 검증
        jwtTokenValidator.validateAccessToken(Authorization);

        //토큰으로 유저 권한 확인 -> ADMIN 이 아니면 에러
        if (!jwtTokenResolver.getUserRole(Authorization).equals("ADMIN")) throw new AccountException(ErrorType.USER_RESTRICTED);

        return userAdminService.getReportedPostList();
    }
}
