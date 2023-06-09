package usw.suwiki.domain.restrictinguser.controller;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "정지 사유 불러오기")
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @GetMapping("/restricted-reason")
    public ResponseForm loadRestrictedReason(
            @Valid @RequestHeader String Authorization
    ) {
        return ResponseForm.success(userBusinessService.executeLoadRestrictedReason(Authorization));
    }
}
