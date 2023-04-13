package usw.suwiki.domain.evaluation.controller;

import java.io.IOException;
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
import usw.suwiki.domain.evaluation.FindByLectureToJson;
import usw.suwiki.domain.evaluation.dto.EvaluatePostsSaveDto;
import usw.suwiki.domain.evaluation.dto.EvaluatePostsUpdateDto;
import usw.suwiki.domain.evaluation.dto.EvaluateResponseByLectureIdDto;
import usw.suwiki.domain.evaluation.dto.EvaluateResponseByUserIdxDto;
import usw.suwiki.domain.evaluation.service.EvaluatePostsService;
import usw.suwiki.global.PageOption;
import usw.suwiki.global.ResponseForm;
import usw.suwiki.global.annotation.ApiLogger;
import usw.suwiki.global.exception.ErrorType;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.jwt.JwtTokenValidator;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "/evaluate-posts")
public class EvaluateController {

    private final EvaluatePostsService evaluatePostsService;
    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenResolver jwtTokenResolver;

    @ApiLogger(option = "evaluatePosts")
    @GetMapping
    public ResponseEntity<FindByLectureToJson> findByLecture(@RequestHeader String Authorization,
        @RequestParam Long lectureId,
        @RequestParam(required = false) Optional<Integer> page) {
        HttpHeaders header = new HttpHeaders();
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization)) {
                throw new AccountException(ErrorType.USER_RESTRICTED);
            }
            List<EvaluateResponseByLectureIdDto> list = evaluatePostsService.findEvaluatePostsByLectureId(
                new PageOption(page), lectureId);
            FindByLectureToJson data = new FindByLectureToJson(list);
            if (evaluatePostsService.verifyIsUserWriteEvaluatePost(
                jwtTokenResolver.getId(Authorization), lectureId)) {
                data.setWritten(false);
            }
            return new ResponseEntity<>(data, header, HttpStatus.valueOf(200));
        } else {
            throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
        }
    }

    @ApiLogger(option = "evaluatePosts")
    @PutMapping
    public ResponseEntity<String> updateEvaluatePosts(@RequestParam Long evaluateIdx,
        @RequestHeader String Authorization, @RequestBody EvaluatePostsUpdateDto dto) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization)) {
                throw new AccountException(ErrorType.USER_RESTRICTED);
            }
            evaluatePostsService.update(evaluateIdx, dto);
            return new ResponseEntity<String>("success", header, HttpStatus.valueOf(200));
        } else {
            throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
        }
    }

    @ApiLogger(option = "evaluatePosts")
    @PostMapping
    public ResponseEntity<String> saveEvaluatePosts(@RequestParam Long lectureId,
        @RequestHeader String Authorization, @RequestBody EvaluatePostsSaveDto dto)
        throws IOException {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);

        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization)) {
                throw new AccountException(ErrorType.USER_RESTRICTED);
            }
            Long userIdx = jwtTokenResolver.getId(Authorization);
            if (evaluatePostsService.verifyIsUserWriteEvaluatePost(userIdx, lectureId)) {
                evaluatePostsService.save(dto, userIdx, lectureId);
                return new ResponseEntity<String>("success", header, HttpStatus.valueOf(200));
            } else {
                throw new AccountException(ErrorType.POSTS_WRITE_OVERLAP);
            }
        } else {
            throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
        }
    }

    @ApiLogger(option = "evaluatePosts")
    @GetMapping("/written") // 이름 수정 , 널값 처리 프론트
    public ResponseEntity<ResponseForm> findByUser(@RequestHeader String Authorization,
        @RequestParam(required = false) Optional<Integer> page) {
        HttpHeaders header = new HttpHeaders();
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization)) {
                throw new AccountException(ErrorType.USER_RESTRICTED);
            }
            List<EvaluateResponseByUserIdxDto> list = evaluatePostsService.findEvaluatePostsByUserId(
                new PageOption(page),
                jwtTokenResolver.getId(Authorization));

            ResponseForm data = new ResponseForm(list);
            return new ResponseEntity<ResponseForm>(data, header, HttpStatus.valueOf(200));

        } else {
            throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
        }
    }

    @ApiLogger(option = "evaluatePosts")
    @DeleteMapping
    public ResponseEntity<String> deleteEvaluatePosts(@RequestParam Long evaluateIdx,
        @RequestHeader String Authorization) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization)) {
                throw new AccountException(ErrorType.USER_RESTRICTED);
            }
            Long userIdx = jwtTokenResolver.getId(Authorization);
            if (evaluatePostsService.deleteEvaluatePost(userIdx, evaluateIdx)) {
                evaluatePostsService.deleteById(evaluateIdx, userIdx);
                return new ResponseEntity<String>("success", header, HttpStatus.valueOf(200));
            } else {
                throw new AccountException(ErrorType.USER_POINT_LACK);
            }
        } else {
            throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
        }
    }
}
