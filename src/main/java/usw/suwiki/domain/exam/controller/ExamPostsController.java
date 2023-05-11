package usw.suwiki.domain.exam.controller;

import static usw.suwiki.global.exception.ExceptionType.*;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.exam.controller.dto.ExamPostsSaveDto;
import usw.suwiki.domain.exam.controller.dto.ExamPostsUpdateDto;
import usw.suwiki.domain.exam.controller.dto.ExamResponseByUserIdxDto;
import usw.suwiki.domain.exam.controller.dto.ReadExamPostResponse;
import usw.suwiki.domain.exam.service.ExamPostService;
import usw.suwiki.domain.exam.controller.dto.viewexam.PurchaseHistoryDto;
import usw.suwiki.global.PageOption;
import usw.suwiki.global.ResponseForm;
import usw.suwiki.global.annotation.ApiLogger;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.exception.errortype.ExamPostException;
import usw.suwiki.global.jwt.JwtAgent;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/exam-posts")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ExamPostsController {

    private final JwtAgent jwtAgent;
    private final ExamPostService examPostService;

    @ApiLogger(option = "examPosts")
    @GetMapping
    public ReadExamPostResponse readExamPostApi(
            @RequestHeader String Authorization,
            @RequestParam Long lectureId,
            @RequestParam(required = false) Optional<Integer> page) {

        validateAuth(Authorization);
        Long userId = jwtAgent.getId(Authorization);

        boolean canRead = examPostService.canRead(userId, lectureId);

        if (!canRead) {
            boolean isWrite = examPostService.isWrite(userId, lectureId);
            return ReadExamPostResponse.ForbiddenToRead(isWrite);
        }

        //시험정보 데이터 존재 여부
        ReadExamPostResponse response = examPostService.readExamPost(userId, lectureId,
                new PageOption(page));

        return response;
    }

    @ApiLogger(option = "examPosts")
    @PostMapping("/purchase")
    public ResponseEntity<String> buyExamInfoApi(
            @RequestHeader String Authorization,
            @RequestParam Long lectureId) {
        validateAuth(Authorization);
        Long userId = jwtAgent.getId(Authorization);

        boolean alreadyBuy = examPostService.canRead(userId, lectureId);

        if (alreadyBuy) {
            throw new ExamPostException(EXAM_POST_ALREADY_PURCHASE);
        }
        examPostService.purchase(lectureId, userId);

        return ResponseEntity.ok("success");
    }

    @ApiLogger(option = "examPosts")
    @PostMapping
    public ResponseEntity<String> writeExamPostApi(
            @RequestParam Long lectureId,
            @RequestBody ExamPostsSaveDto requestBody,
            @RequestHeader String Authorization
    ) {
        validateAuth(Authorization);
        Long userIdx = jwtAgent.getId(Authorization);

        if (examPostService.isWrite(userIdx, lectureId)) {
            throw new AccountException(POSTS_WRITE_OVERLAP);
        }
        examPostService.write(requestBody, userIdx, lectureId);
        return ResponseEntity.ok("success");
    }

    @ApiLogger(option = "examPosts")
    @PutMapping
    public ResponseEntity<String> updateExamPostsApi(
            @RequestParam Long examIdx,
            @RequestHeader String Authorization,
            @RequestBody ExamPostsUpdateDto dto
    ) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        jwtAgent.validateJwt(Authorization);
        if (jwtAgent.getUserIsRestricted(Authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }
        examPostService.update(examIdx, dto);
        return new ResponseEntity<>("success", header, HttpStatus.valueOf(200));
    }

    @ApiLogger(option = "examPosts")
    @GetMapping("/written") // 이름 수정 , 널값 처리 프론트
    public ResponseForm findExamPostsByUserApi(
        @RequestHeader String Authorization,
        @RequestParam(required = false) Optional<Integer> page
    ) {
        validateAuth(Authorization);
        Long userIdx = jwtAgent.getId(Authorization);
        PageOption option = new PageOption(page);

        List<ExamResponseByUserIdxDto> response = examPostService.readExamPostByUserIdAndOption(
            option, userIdx);

        return new ResponseForm(response);
    }

    @ApiLogger(option = "examPosts")
    @DeleteMapping
    public ResponseEntity<String> deleteExamPosts(
            @RequestParam Long examIdx,
            @RequestHeader String Authorization
    ) {
        validateAuth(Authorization);
        Long userIdx = jwtAgent.getId(Authorization);
        examPostService.executeDeleteExamPosts(userIdx, examIdx);
        return ResponseEntity.ok("success");
    }

    @ApiLogger(option = "examPosts")
    @GetMapping("/purchase") // 이름 수정 , 널값 처리 프론트
    public ResponseForm readPurchaseHistoryApi(
        @RequestHeader String Authorization
    ) {
        jwtAgent.validateJwt(Authorization);
        Long userId = jwtAgent.getId(Authorization);

        List<PurchaseHistoryDto> response = examPostService.readPurchaseHistory(userId);

        return new ResponseForm(response);
    }

    private void validateAuth(String authorization) {
        jwtAgent.validateJwt(authorization);
        if (jwtAgent.getUserIsRestricted(authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }
    }
}
