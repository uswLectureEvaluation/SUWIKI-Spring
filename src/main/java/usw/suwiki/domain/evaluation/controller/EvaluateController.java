package usw.suwiki.domain.evaluation.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static usw.suwiki.global.exception.ExceptionType.POSTS_WRITE_OVERLAP;
import static usw.suwiki.global.exception.ExceptionType.USER_RESTRICTED;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import usw.suwiki.domain.evaluation.FindByLectureToJson;
import usw.suwiki.domain.evaluation.dto.EvaluatePostsSaveDto;
import usw.suwiki.domain.evaluation.dto.EvaluatePostsUpdateDto;
import usw.suwiki.domain.evaluation.dto.EvaluateResponseByLectureIdDto;
import usw.suwiki.domain.evaluation.dto.EvaluateResponseByUserIdxDto;
import usw.suwiki.domain.evaluation.service.EvaluatePostsService;
import usw.suwiki.global.PageOption;
import usw.suwiki.global.ResponseForm;
import usw.suwiki.global.annotation.ApiLogger;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtResolver;
import usw.suwiki.global.jwt.JwtValidator;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "/evaluate-posts")
public class EvaluateController {

    private final EvaluatePostsService evaluatePostsService;
    private final JwtValidator jwtValidator;
    private final JwtResolver jwtResolver;

    @ApiLogger(option = "evaluatePosts")
    @GetMapping
    public ResponseEntity<FindByLectureToJson> findByLecture(
        @RequestHeader String Authorization,
        @RequestParam Long lectureId,
        @RequestParam(required = false) Optional<Integer> page
    ) {
        HttpHeaders header = new HttpHeaders();
        jwtValidator.validateJwt(Authorization);
        if (jwtResolver.getUserIsRestricted(Authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }
        List<EvaluateResponseByLectureIdDto> list =
            evaluatePostsService.findEvaluatePostsByLectureId(
                new PageOption(page),
                lectureId
            );
        FindByLectureToJson data = new FindByLectureToJson(list);
        if (evaluatePostsService.verifyIsUserWriteEvaluatePost(
            jwtResolver.getId(Authorization), lectureId)) {
            data.setWritten(false);
        }
        return new ResponseEntity<>(data, header, HttpStatus.valueOf(200));
    }

    @ApiLogger(option = "evaluatePosts")
    @PutMapping
    public ResponseEntity<String> updateEvaluatePosts(@RequestParam Long evaluateIdx,
        @RequestHeader String Authorization, @RequestBody EvaluatePostsUpdateDto dto) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(APPLICATION_JSON);
        jwtValidator.validateJwt(Authorization);
        if (jwtResolver.getUserIsRestricted(Authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }
        evaluatePostsService.update(evaluateIdx, dto);
        return new ResponseEntity<>("success", header, HttpStatus.valueOf(200));
    }

    @ApiLogger(option = "evaluatePosts")
    @PostMapping
    public ResponseEntity<String> saveEvaluatePosts(
        @RequestParam Long lectureId,
        @RequestHeader String Authorization,
        @RequestBody EvaluatePostsSaveDto dto
    ) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(APPLICATION_JSON);

        jwtValidator.validateJwt(Authorization);
        if (jwtResolver.getUserIsRestricted(Authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }
        Long userIdx = jwtResolver.getId(Authorization);
        if (evaluatePostsService.verifyIsUserWriteEvaluatePost(userIdx, lectureId)) {
            evaluatePostsService.save(dto, userIdx, lectureId);
            return new ResponseEntity<>("success", header, HttpStatus.valueOf(200));
        } else {
            throw new AccountException(POSTS_WRITE_OVERLAP);
        }
    }

    @ApiLogger(option = "evaluatePosts")
    @GetMapping("/written") // 이름 수정 , 널값 처리 프론트
    public ResponseEntity<ResponseForm> findByUser(
        @RequestHeader String Authorization,
        @RequestParam(required = false) Optional<Integer> page
    ) {
        HttpHeaders header = new HttpHeaders();
        jwtValidator.validateJwt(Authorization);
        if (jwtResolver.getUserIsRestricted(Authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }
        List<EvaluateResponseByUserIdxDto> list = evaluatePostsService.findEvaluatePostsByUserId(
            new PageOption(page),
            jwtResolver.getId(Authorization));

        ResponseForm data = new ResponseForm(list);
        return new ResponseEntity<>(data, header, HttpStatus.valueOf(200));
    }

    @ApiLogger(option = "evaluatePosts")
    @DeleteMapping
    public ResponseEntity<String> deleteEvaluatePosts(@RequestParam Long evaluateIdx,
        @RequestHeader String Authorization) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(APPLICATION_JSON);
        jwtValidator.validateJwt(Authorization);
        if (jwtResolver.getUserIsRestricted(Authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }
        Long userIdx = jwtResolver.getId(Authorization);
        if (evaluatePostsService.deleteEvaluatePost(userIdx, evaluateIdx)) {
            evaluatePostsService.deleteById(evaluateIdx, userIdx);
            return new ResponseEntity<>("success", header, HttpStatus.valueOf(200));
        } else {
            throw new AccountException(ExceptionType.USER_POINT_LACK);
        }
    }
}
