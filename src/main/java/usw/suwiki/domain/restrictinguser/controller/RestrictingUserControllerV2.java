package usw.suwiki.domain.restrictinguser.controller;

import static org.springframework.http.HttpStatus.OK;

import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.domain.user.user.service.UserBusinessService;
import usw.suwiki.global.ResponseForm;
import usw.suwiki.global.annotation.ApiLogger;

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
