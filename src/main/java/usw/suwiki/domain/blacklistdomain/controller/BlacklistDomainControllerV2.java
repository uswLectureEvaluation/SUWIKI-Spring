package usw.suwiki.domain.blacklistdomain.controller;

import static org.springframework.http.HttpStatus.OK;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.domain.user.user.controller.dto.UserResponseDto.LoadMyBlackListReasonResponseForm;
import usw.suwiki.domain.user.user.service.UserBusinessService;
import usw.suwiki.global.annotation.ApiLogger;

@RestController
@RequestMapping("/v2/blacklist")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BlacklistDomainControllerV2 {

    private final UserBusinessService userBusinessService;

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @GetMapping("/logs")
    public List<LoadMyBlackListReasonResponseForm> loadBlacklistReason(
        @Valid @RequestHeader String Authorization) {
        return userBusinessService.executeLoadBlackListReason(Authorization);
    }
}
