package usw.suwiki.domain.exam.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
import usw.suwiki.domain.exam.FindByLectureToExam;
import usw.suwiki.domain.exam.dto.ExamPostsSaveDto;
import usw.suwiki.domain.exam.dto.ExamPostsUpdateDto;
import usw.suwiki.domain.exam.dto.ExamResponseByLectureIdDto;
import usw.suwiki.domain.exam.dto.ExamResponseByUserIdxDto;
import usw.suwiki.domain.exam.service.ExamPostsService;
import usw.suwiki.domain.viewExam.dto.PurchaseHistoryDto;
import usw.suwiki.domain.viewExam.service.ViewExamService;
import usw.suwiki.global.PageOption;
import usw.suwiki.global.ToJsonArray;
import usw.suwiki.global.annotation.ApiLogger;
import usw.suwiki.global.exception.ErrorType;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.jwt.JwtTokenValidator;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/exam-posts")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ExamPostsController {

    private final ExamPostsService examPostsService;
    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenResolver jwtTokenResolver;
    private final ViewExamService viewExamService;

    @ApiLogger(option = "examPosts")
    @GetMapping
    public ResponseEntity<FindByLectureToExam> findByLecture(@RequestParam Long lectureId,
        @RequestHeader String Authorization,
        @RequestParam(required = false) Optional<Integer> page) {
        HttpHeaders header = new HttpHeaders();
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization)) {
                throw new AccountException(ErrorType.USER_RESTRICTED);
            }
            List<ExamResponseByLectureIdDto> list = examPostsService.findExamPostsByLectureId(
                new PageOption(page), lectureId);
            FindByLectureToExam data = new FindByLectureToExam(list);
            if (examPostsService.verifyWriteExamPosts(jwtTokenResolver.getId(Authorization),
                lectureId)) {
                data.setWritten(false);
            }
            if (list.isEmpty()) {
                data.setExamDataExist(false);
                return new ResponseEntity<FindByLectureToExam>(data, header,
                    HttpStatus.valueOf(200));
            } else {
                if (viewExamService.verifyAuth(lectureId, jwtTokenResolver.getId(Authorization))) {
                    return new ResponseEntity<FindByLectureToExam>(data, header,
                        HttpStatus.valueOf(200));
                } else {
                    data.setData(new ArrayList<>());
                    return new ResponseEntity<FindByLectureToExam>(data, header,
                        HttpStatus.valueOf(200));
                }
            }
        } else {
            throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
        }
    }

    @ApiLogger(option = "examPosts")
    @PostMapping("/purchase")
    public ResponseEntity<String> buyExamInfo(@RequestParam Long lectureId,
        @RequestHeader String Authorization) {
        HttpHeaders header = new HttpHeaders();
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization)) {
                throw new AccountException(ErrorType.USER_RESTRICTED);
            }
            viewExamService.save(lectureId, jwtTokenResolver.getId(Authorization));
            return new ResponseEntity<String>("success", header, HttpStatus.valueOf(200));
        } else {
            throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
        }
    }

    @ApiLogger(option = "examPosts")
    @PostMapping
    public ResponseEntity<String> saveExamPosts(@RequestParam Long lectureId,
        @RequestBody ExamPostsSaveDto dto, @RequestHeader String Authorization) throws IOException {
        HttpHeaders header = new HttpHeaders();
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization)) {
                throw new AccountException(ErrorType.USER_RESTRICTED);
            }
            Long userIdx = jwtTokenResolver.getId(Authorization);
            if (examPostsService.verifyWriteExamPosts(userIdx, lectureId)) {
                examPostsService.save(dto, userIdx, lectureId);
                return new ResponseEntity<String>("success", header, HttpStatus.valueOf(200));
            } else {
                throw new AccountException(ErrorType.POSTS_WRITE_OVERLAP);
            }
        } else {
            throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
        }
    }

    @ApiLogger(option = "examPosts")
    @PutMapping
    public ResponseEntity<String> updateExamPosts(@RequestParam Long examIdx,
        @RequestHeader String Authorization, @RequestBody ExamPostsUpdateDto dto) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization)) {
                throw new AccountException(ErrorType.USER_RESTRICTED);
            }
            examPostsService.update(examIdx, dto);
            return new ResponseEntity<String>("success", header, HttpStatus.valueOf(200));
        } else {
            throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
        }
    }

    @ApiLogger(option = "examPosts")
    @GetMapping("/written") // 이름 수정 , 널값 처리 프론트
    public ResponseEntity<ToJsonArray> findByUser(@RequestHeader String Authorization,
        @RequestParam(required = false) Optional<Integer> page) {
        HttpHeaders header = new HttpHeaders();
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization)) {
                throw new AccountException(ErrorType.USER_RESTRICTED);
            }
            List<ExamResponseByUserIdxDto> list = examPostsService.findExamPostsByUserId(
                new PageOption(page),
                jwtTokenResolver.getId(Authorization));

            ToJsonArray data = new ToJsonArray(list);
            return new ResponseEntity<ToJsonArray>(data, header, HttpStatus.valueOf(200));

        } else {
            throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
        }
    }

    @ApiLogger(option = "examPosts")
    @DeleteMapping
    public ResponseEntity<String> deleteExamPosts(@RequestParam Long examIdx,
        @RequestHeader String Authorization) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization)) {
                throw new AccountException(ErrorType.USER_RESTRICTED);
            }
            Long userIdx = jwtTokenResolver.getId(Authorization);
            if (examPostsService.verifyDeleteExamPosts(userIdx, examIdx)) {
                examPostsService.deleteById(examIdx, userIdx);
                return new ResponseEntity<String>("success", header, HttpStatus.valueOf(200));
            } else {
                throw new AccountException(ErrorType.USER_POINT_LACK);
            }
        } else {
            throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
        }
    }

    @ApiLogger(option = "examPosts")
    @GetMapping("/purchase") // 이름 수정 , 널값 처리 프론트
    public ResponseEntity<ToJsonArray> showPurchaseHistory(@RequestHeader String Authorization) {
        HttpHeaders header = new HttpHeaders();
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            Long userIdx = jwtTokenResolver.getId(Authorization);
            List<PurchaseHistoryDto> list = viewExamService.findByUserId(userIdx);
            ToJsonArray data = new ToJsonArray(list);
            return new ResponseEntity<ToJsonArray>(data, header, HttpStatus.valueOf(200));

        } else {
            throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
        }
    }
}
