package usw.suwiki.domain.exam.controller;

import java.util.List;
import java.util.Optional;
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
import usw.suwiki.domain.exam.controller.dto.ReadExamPostResponse;
import usw.suwiki.domain.exam.controller.dto.ExamPostsSaveDto;
import usw.suwiki.domain.exam.controller.dto.ExamPostsUpdateDto;
import usw.suwiki.domain.exam.controller.dto.ExamResponseByUserIdxDto;
import usw.suwiki.domain.exam.service.ExamPostsService;
import usw.suwiki.domain.viewExam.dto.PurchaseHistoryDto;
import usw.suwiki.domain.viewExam.service.ViewExamService;
import usw.suwiki.global.PageOption;
import usw.suwiki.global.ResponseForm;
import usw.suwiki.global.annotation.ApiLogger;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtResolver;
import usw.suwiki.global.jwt.JwtValidator;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/exam-posts")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ExamPostsController {

    private final ExamPostsService examPostsService;
    private final JwtValidator jwtValidator;
    private final JwtResolver jwtResolver;
    private final ViewExamService viewExamService;

    @ApiLogger(option = "examPosts")
    @GetMapping
    public ReadExamPostResponse readExamPostApi(
        @RequestHeader String Authorization,
        @RequestParam Long lectureId,
        @RequestParam(required = false) Optional<Integer> page) {

        validateAuth(Authorization);
        Long userId = jwtResolver.getId(Authorization);

        boolean canRead = viewExamService.validateReadExamPost(userId, lectureId);
        System.out.println("canRead : " + canRead);

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
        @RequestParam Long lectureId,
        @RequestHeader String Authorization
    ) {
        validateAuth(Authorization);
        viewExamService.open(lectureId, jwtResolver.getId(Authorization));

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
        Long userIdx = jwtResolver.getId(Authorization);

        if (examPostsService.isWrite(userIdx, lectureId)) {
            throw new AccountException(ExceptionType.POSTS_WRITE_OVERLAP);
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
        jwtValidator.validateJwt(Authorization);
        if (jwtResolver.getUserIsRestricted(Authorization)) {
            throw new AccountException(ExceptionType.USER_RESTRICTED);
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
        jwtValidator.validateJwt(Authorization);
        if (jwtResolver.getUserIsRestricted(Authorization)) {
            throw new AccountException(ExceptionType.USER_RESTRICTED);
        }
        List<ExamResponseByUserIdxDto> list = examPostsService.findExamPostsByUserId(
            new PageOption(page),
            jwtResolver.getId(Authorization));

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
        Long userIdx = jwtResolver.getId(Authorization);
        if (examPostsService.verifyDeleteExamPosts(userIdx, examIdx)) {
            examPostsService.deleteById(examIdx, userIdx);
            return ResponseEntity.ok("success");
        } else {
            throw new AccountException(ExceptionType.USER_POINT_LACK);
        }
    }

    @ApiLogger(option = "examPosts")
    @GetMapping("/purchase") // 이름 수정 , 널값 처리 프론트
    public ResponseEntity<ResponseForm> showPurchaseHistory(@RequestHeader String Authorization) {
        jwtValidator.validateJwt(Authorization);
        Long userId = jwtResolver.getId(Authorization);

        List<PurchaseHistoryDto> list = viewExamService.findByUserId(userId);
        ResponseForm data = new ResponseForm(list);

        return ResponseEntity.ok(data);
    }

    private void validateAuth(String authorization) {
        jwtValidator.validateJwt(authorization);
        if (jwtResolver.getUserIsRestricted(authorization)) {
            throw new AccountException(ExceptionType.USER_RESTRICTED);
        }
    }
}
