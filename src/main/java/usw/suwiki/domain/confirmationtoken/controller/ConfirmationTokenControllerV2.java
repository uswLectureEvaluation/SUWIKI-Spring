package usw.suwiki.domain.confirmationtoken.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.confirmationtoken.service.ConfirmationTokenBusinessService;
import usw.suwiki.global.annotation.ApiLogger;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/v2/confirmation-token")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ConfirmationTokenControllerV2 {

    private final ConfirmationTokenBusinessService confirmationTokenBusinessService;

    // 이메일 인증 링크를 눌렀을 때
    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @PostMapping("verify")
    public String confirmEmail(@RequestParam("token") String token) {
        return confirmationTokenBusinessService.confirmToken(token);
    }
}
