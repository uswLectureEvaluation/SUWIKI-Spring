package usw.suwiki.domain.restrictinguser.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.user.user.controller.dto.UserResponseDto.LoadMyRestrictedReasonResponseForm;
import usw.suwiki.domain.user.user.service.UserBusinessService;
import usw.suwiki.global.annotation.ApiLogger;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/v2/favorite-major")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestrictingUserControllerV2 {

    private final UserBusinessService userBusinessService;

    // 정지 사유 불러오기
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @GetMapping("/restricted-reason")
    public List<LoadMyRestrictedReasonResponseForm> loadRestrictedReason(
            @Valid @RequestHeader String Authorization
    ) {
        return userBusinessService.executeLoadRestrictedReason(Authorization);
    }
}
