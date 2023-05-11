package usw.suwiki.domain.blacklistdomain.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.user.user.controller.dto.UserResponseDto.LoadMyBlackListReasonResponseForm;
import usw.suwiki.domain.user.user.service.UserBusinessService;
import usw.suwiki.global.annotation.ApiLogger;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/v2/blacklist")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BlacklistDomainControllerV2 {

    private final UserBusinessService userBusinessService;

    // 블랙리스트 사유 불러오기
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @GetMapping("/logs")
    public List<LoadMyBlackListReasonResponseForm> loadBlacklistReason(
            @Valid @RequestHeader String Authorization) {
        return userBusinessService.executeLoadBlackListReason(Authorization);
    }
}
