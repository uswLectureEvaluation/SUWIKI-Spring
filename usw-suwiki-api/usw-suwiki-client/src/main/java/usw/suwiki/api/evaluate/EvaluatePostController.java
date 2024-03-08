package usw.suwiki.api.evaluate;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.core.jwt.JwtAgent;
import usw.suwiki.common.pagination.PageOption;
import usw.suwiki.common.response.ResponseForm;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.domain.evaluatepost.dto.EvaluatePostSaveDto;
import usw.suwiki.domain.evaluatepost.dto.EvaluatePostUpdateDto;
import usw.suwiki.domain.evaluatepost.dto.FindByLectureToJson;
import usw.suwiki.domain.evaluatepost.service.EvaluatePostService;
import usw.suwiki.statistics.annotation.ApiLogger;

import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "/evaluate-posts")
public class EvaluatePostController {
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
            throw new AccountException(ExceptionType.USER_RESTRICTED);
        }

        Long userIdx = jwtAgent.getId(Authorization);

        return evaluatePostService.readEvaluatePostsByLectureId(new PageOption(page), userIdx, lectureId);
    }

    @ApiLogger(option = "evaluatePosts")
    @PutMapping
    public String updateEvaluatePosts(
        @RequestParam Long evaluateIdx,
        @RequestHeader String Authorization,
        @RequestBody EvaluatePostUpdateDto requestBody
    ) {
        jwtAgent.validateJwt(Authorization);
        if (jwtAgent.getUserIsRestricted(Authorization)) {
            throw new AccountException(ExceptionType.USER_RESTRICTED);
        }

        evaluatePostService.update(evaluateIdx, requestBody);
        return "success";
    }

    @ApiLogger(option = "evaluatePosts")
    @PostMapping
    public String writeEvaluatePostApi(
        @RequestParam Long lectureId,
        @RequestHeader String Authorization,
        @RequestBody EvaluatePostSaveDto requestBody
    ) {
        jwtAgent.validateJwt(Authorization);
        if (jwtAgent.getUserIsRestricted(Authorization)) {
            throw new AccountException(ExceptionType.USER_RESTRICTED);
        }

        Long userIdx = jwtAgent.getId(Authorization);
        evaluatePostService.write(requestBody, userIdx, lectureId);

        return "success";
    }

    @ApiLogger(option = "evaluatePosts")
    @GetMapping("/written")
    @ResponseStatus(OK)
    public ResponseForm findByUser(
        @RequestHeader String Authorization,
        @RequestParam(required = false) Optional<Integer> page
    ) {
        jwtAgent.validateJwt(Authorization);
        if (jwtAgent.getUserIsRestricted(Authorization)) {
            throw new AccountException(ExceptionType.USER_RESTRICTED);
        }

        return new ResponseForm(
          evaluatePostService.readEvaluatePostsByUserId(new PageOption(page), jwtAgent.getId(Authorization))
        );
    }

    @ApiLogger(option = "evaluatePosts")
    @DeleteMapping
    @ResponseStatus(OK)
    public String deleteEvaluatePosts(@RequestParam Long evaluateIdx, @RequestHeader String Authorization) {
        jwtAgent.validateJwt(Authorization);

        if (jwtAgent.getUserIsRestricted(Authorization)) {
            throw new AccountException(ExceptionType.USER_RESTRICTED);
        }

        Long userIdx = jwtAgent.getId(Authorization);
        evaluatePostService.executeDeleteEvaluatePost(evaluateIdx, userIdx);
        return "success";
    }
}
