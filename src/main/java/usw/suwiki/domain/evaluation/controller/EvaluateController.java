package usw.suwiki.domain.evaluation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.evaluation.service.dto.FindByLectureToJson;
import usw.suwiki.domain.evaluation.controller.dto.EvaluatePostsSaveDto;
import usw.suwiki.domain.evaluation.controller.dto.EvaluatePostsUpdateDto;
import usw.suwiki.domain.evaluation.controller.dto.EvaluateResponseByLectureIdDto;
import usw.suwiki.domain.evaluation.controller.dto.EvaluateResponseByUserIdxDto;
import usw.suwiki.domain.evaluation.service.EvaluatePostService;
import usw.suwiki.global.PageOption;
import usw.suwiki.global.ResponseForm;
import usw.suwiki.global.annotation.ApiLogger;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.exception.errortype.EvaluatePostException;
import usw.suwiki.global.jwt.JwtAgent;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static usw.suwiki.global.exception.ExceptionType.POSTS_WRITE_OVERLAP;
import static usw.suwiki.global.exception.ExceptionType.USER_RESTRICTED;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "/evaluate-posts")
public class EvaluateController {

    private final EvaluatePostService evaluatePostService;
    private final JwtAgent jwtAgent;

    @ApiLogger(option = "evaluatePosts")
    @GetMapping
    public FindByLectureToJson readEvaluatePostsByLectureApi(
        @RequestHeader String Authorization,
        @RequestParam Long lectureId,
        @RequestParam(required = false) Optional<Integer> page) {

        jwtAgent.validateJwt(Authorization);
        if (jwtAgent.getUserIsRestricted(Authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }
        Long userIdx = jwtAgent.getId(Authorization);

        FindByLectureToJson response = evaluatePostService.readEvaluatePostsByLectureId(new PageOption(page),
            userIdx, lectureId);

        return response;
    }

    @ApiLogger(option = "evaluatePosts")
    @PutMapping
    public String updateEvaluatePosts(
            @RequestParam Long evaluateIdx,
            @RequestHeader String Authorization,
            @RequestBody EvaluatePostsUpdateDto requestBody
    ) {
        jwtAgent.validateJwt(Authorization);
        if (jwtAgent.getUserIsRestricted(Authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }

        evaluatePostService.update(evaluateIdx, requestBody);
        return "success";
    }

    @ApiLogger(option = "evaluatePosts")
    @PostMapping
    public String writeEvaluatePostApi(
        @RequestParam Long lectureId,
        @RequestHeader String Authorization,
        @RequestBody EvaluatePostsSaveDto requestBody) {

        jwtAgent.validateJwt(Authorization);
        if (jwtAgent.getUserIsRestricted(Authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }

        Long userIdx = jwtAgent.getId(Authorization);
        evaluatePostService.write(requestBody, userIdx, lectureId);

        return "success";
    }

    @ApiLogger(option = "evaluatePosts")
    @ResponseStatus(OK)
    @GetMapping("/written")
    public ResponseForm findByUser(
            @RequestHeader String Authorization,
            @RequestParam(required = false) Optional<Integer> page
    ) {
        jwtAgent.validateJwt(Authorization);
        if (jwtAgent.getUserIsRestricted(Authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }

        List<EvaluateResponseByUserIdxDto> list = evaluatePostService.readEvaluatePostsByUserId(
            new PageOption(page),
            jwtAgent.getId(Authorization)
        );

        ResponseForm response = new ResponseForm(list);
        return response;
    }

    @ApiLogger(option = "evaluatePosts")
    @ResponseStatus(OK)
    @DeleteMapping
    public String deleteEvaluatePosts(
            @RequestParam Long evaluateIdx,
            @RequestHeader String Authorization
    ) {
        jwtAgent.validateJwt(Authorization);
        if (jwtAgent.getUserIsRestricted(Authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }
        Long userIdx = jwtAgent.getId(Authorization);
        evaluatePostService.executeDeleteEvaluatePost(evaluateIdx, userIdx);
        return "success";
    }
}