package usw.suwiki.api.exam;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.core.annotation.JWTVerify;
import usw.suwiki.auth.core.jwt.JwtAgent;
import usw.suwiki.common.pagination.PageOption;
import usw.suwiki.common.response.ResponseForm;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExamPostException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.domain.exampost.dto.ExamPostUpdateDto;
import usw.suwiki.domain.exampost.dto.ExamPostsSaveDto;
import usw.suwiki.domain.exampost.dto.ReadExamPostResponse;
import usw.suwiki.domain.exampost.service.ExamPostService;
import usw.suwiki.statistics.annotation.ApiLogger;

import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/exam-posts")
@RequiredArgsConstructor
public class ExamPostsController {
    private final ExamPostService examPostService;
    private final JwtAgent jwtAgent;

    @ApiLogger(option = "examPosts")
    @GetMapping
    public ReadExamPostResponse readExamPostApi(
      @RequestHeader String Authorization,
      @RequestParam Long lectureId,
      @RequestParam(required = false) Optional<Integer> page
    ) {
        validateAuth(Authorization);
        Long userId = jwtAgent.getId(Authorization);

        if (!examPostService.canRead(userId, lectureId)) {
            ReadExamPostResponse response = examPostService.readExamPost(userId, lectureId, new PageOption(page));
            response.forbiddenToRead();
            return response;
        }

        return examPostService.readExamPost(userId, lectureId, new PageOption(page));
    }

    @ApiLogger(option = "examPosts")
    @PostMapping("/purchase")
    public ResponseEntity<String> buyExamInfoApi(@RequestHeader String Authorization, @RequestParam Long lectureId) {
        validateAuth(Authorization);
        Long userId = jwtAgent.getId(Authorization);

        if (examPostService.canRead(userId, lectureId)) {
            throw new ExamPostException(ExceptionType.EXAM_POST_ALREADY_PURCHASE);
        }
        examPostService.purchase(lectureId, userId);

        return ResponseEntity.ok("success");
    }

    @ApiLogger(option = "examPosts")
    @PostMapping
    public ResponseEntity<String> writeExamPostApi(
      @RequestParam Long lectureId,
      @RequestBody ExamPostsSaveDto request,
      @RequestHeader String Authorization
    ) {
        validateAuth(Authorization);
        Long userIdx = jwtAgent.getId(Authorization);

        if (examPostService.isWrite(userIdx, lectureId)) {
            throw new AccountException(ExceptionType.POSTS_WRITE_OVERLAP);
        }
        examPostService.write(request, userIdx, lectureId);
        return ResponseEntity.ok("success");
    }

    @ApiLogger(option = "examPosts")
    @PutMapping
    public ResponseEntity<String> updateExamPostsApi(
      @RequestParam Long examIdx,
      @RequestHeader String Authorization,
      @RequestBody ExamPostUpdateDto request
    ) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        jwtAgent.validateJwt(Authorization);

        if (jwtAgent.getUserIsRestricted(Authorization)) {
            throw new AccountException(ExceptionType.USER_RESTRICTED);
        }

        examPostService.update(examIdx, request);
        return new ResponseEntity<>("success", header, HttpStatus.valueOf(200));
    }

    @JWTVerify
    @ApiLogger(option = "examPosts")
    @GetMapping("/written")
    public ResponseForm findExamPostsByUserApi(
      @RequestHeader String Authorization,
      @RequestParam(required = false) Optional<Integer> page
    ) {
        Long userIdx = jwtAgent.getId(Authorization);
        PageOption option = new PageOption(page);

        return new ResponseForm(examPostService.readExamPostByUserIdAndOption(option, userIdx));
    }

    @ApiLogger(option = "examPosts")
    @DeleteMapping
    public ResponseEntity<String> deleteExamPosts(@RequestParam Long examIdx, @RequestHeader String Authorization) {
        validateAuth(Authorization);
        Long userIdx = jwtAgent.getId(Authorization);
        examPostService.executeDeleteExamPosts(userIdx, examIdx);
        return ResponseEntity.ok("success");
    }

    @ApiLogger(option = "examPosts")
    @GetMapping("/purchase")
    public ResponseForm readPurchaseHistoryApi(@RequestHeader String Authorization) {
        jwtAgent.validateJwt(Authorization);
        return new ResponseForm(examPostService.readPurchaseHistory(jwtAgent.getId(Authorization)));
    }

    private void validateAuth(String authorization) {
        jwtAgent.validateJwt(authorization);
        if (jwtAgent.getUserIsRestricted(authorization)) {
            throw new AccountException(ExceptionType.USER_RESTRICTED);
        }
    }
}
