package usw.suwiki.domain.restrictinguser.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.user.user.service.UserBusinessService;
import usw.suwiki.global.ResponseForm;
import usw.suwiki.global.annotation.ApiLogger;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/v2/favorite-major")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestrictingUserControllerV2 {

    private final UserBusinessService userBusinessService;

    @ApiOperation(
            value = "정지 사유 불러오기",
            notes = "토큰 정보를 바탕으로 정지 사유를 불러온다."
    )
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @GetMapping("/restricted-reason")
    public ResponseForm loadRestrictedReason(
            @Valid @RequestHeader String Authorization
    ) {
        return ResponseForm.success(userBusinessService.executeLoadRestrictedReason(Authorization));
    }
}
