package usw.suwiki.domain.exam.controller;

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
import usw.suwiki.domain.exam.service.ExamPostsService;
import usw.suwiki.domain.viewExam.dto.PurchaseHistoryDto;
import usw.suwiki.domain.viewExam.service.ViewExamService;
import usw.suwiki.global.PageOption;
import usw.suwiki.global.ResponseForm;
import usw.suwiki.global.annotation.ApiLogger;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.exception.errortype.ExamPostException;
import usw.suwiki.global.jwt.JwtAgent;

import java.util.List;
import java.util.Optional;

import static usw.suwiki.global.exception.ExceptionType.POSTS_WRITE_OVERLAP;
import static usw.suwiki.global.exception.ExceptionType.USER_RESTRICTED;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/exam-posts")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ExamPostsController {

    private final ExamPostsService examPostsService;
    private final JwtAgent jwtAgent;
    private final ViewExamService viewExamService;

    @ApiLogger(option = "examPosts")
    @GetMapping
    public ReadExamPostResponse readExamPostApi(
            @RequestHeader String Authorization,
            @RequestParam Long lectureId,
            @RequestParam(required = false) Optional<Integer> page) {

        validateAuth(Authorization);
        Long userId = jwtAgent.getId(Authorization);

        boolean canRead = viewExamService.isExist(userId, lectureId);

        if (!canRead) {
            boolean isWrite = examPostsService.isWrite(userId, lectureId);
            return ReadExamPostResponse.ForbiddenToRead(isWrite);
        }

        //시험정보 데이터 존재 여부
        ReadExamPostResponse response = examPostsService.readExamPost(userId, lectureId,
                new PageOption(page));

        return response;
    }

    @ApiLogger(option = "examPosts")
    @PostMapping("/purchase")
    public ResponseEntity<String> buyExamInfo(
            @RequestHeader String Authorization,
            @RequestParam Long lectureId) {
        validateAuth(Authorization);
        Long userId = jwtAgent.getId(Authorization);

        boolean exist = viewExamService.isExist(userId, lectureId);
        if (exist) {
            throw new ExamPostException(POSTS_WRITE_OVERLAP);
        }
        viewExamService.open(lectureId, userId);

        return ResponseEntity.ok("success");
    }

    @ApiLogger(option = "examPosts")
    @PostMapping
    public ResponseEntity<String> writeExamPostApi(
            @RequestParam Long lectureId,
            @RequestBody ExamPostsSaveDto dto,
            @RequestHeader String Authorization
    ) {
        validateAuth(Authorization);
        Long userIdx = jwtAgent.getId(Authorization);

        if (examPostsService.isWrite(userIdx, lectureId)) {
            throw new AccountException(POSTS_WRITE_OVERLAP);
        }
        examPostsService.write(dto, userIdx, lectureId);
        return ResponseEntity.ok("success");
    }

    @ApiLogger(option = "examPosts")
    @PutMapping
    public ResponseEntity<String> updateExamPosts(
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
        examPostsService.update(examIdx, dto);
        return new ResponseEntity<>("success", header, HttpStatus.valueOf(200));
    }

    @ApiLogger(option = "examPosts")
    @GetMapping("/written") // 이름 수정 , 널값 처리 프론트
    public ResponseEntity<ResponseForm> findByUser(
            @RequestHeader String Authorization,
            @RequestParam(required = false) Optional<Integer> page
    ) {
        HttpHeaders header = new HttpHeaders();
        jwtAgent.validateJwt(Authorization);
        if (jwtAgent.getUserIsRestricted(Authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }
        List<ExamResponseByUserIdxDto> list = examPostsService.findExamPostsByUserId(
                new PageOption(page),
                jwtAgent.getId(Authorization));

        ResponseForm data = new ResponseForm(list);
        return new ResponseEntity<>(data, header, HttpStatus.valueOf(200));
    }

    @ApiLogger(option = "examPosts")
    @DeleteMapping
    public ResponseEntity<String> deleteExamPosts(
            @RequestParam Long examIdx,
            @RequestHeader String Authorization
    ) {
        validateAuth(Authorization);
        Long userIdx = jwtAgent.getId(Authorization);
        examPostsService.executeDeleteExamPosts(userIdx, examIdx);
        return ResponseEntity.ok("success");
    }

    @ApiLogger(option = "examPosts")
    @GetMapping("/purchase") // 이름 수정 , 널값 처리 프론트
    public ResponseEntity<ResponseForm> showPurchaseHistory(@RequestHeader String Authorization) {
        jwtAgent.validateJwt(Authorization);
        Long userId = jwtAgent.getId(Authorization);

        List<PurchaseHistoryDto> list = viewExamService.findByUserId(userId);
        ResponseForm data = new ResponseForm(list);

        return ResponseEntity.ok(data);
    }

    private void validateAuth(String authorization) {
        jwtAgent.validateJwt(authorization);
        if (jwtAgent.getUserIsRestricted(authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }
    }
}
