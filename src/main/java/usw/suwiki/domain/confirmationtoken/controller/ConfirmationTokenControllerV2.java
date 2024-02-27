package usw.suwiki.domain.confirmationtoken.controller;

import static org.springframework.http.HttpStatus.OK;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.domain.confirmationtoken.service.ConfirmationTokenBusinessService;
import usw.suwiki.global.annotation.ApiLogger;

@RestController
@RequestMapping("/v2/confirmation-token")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ConfirmationTokenControllerV2 {

    private final ConfirmationTokenBusinessService confirmationTokenBusinessService;

    @ResponseStatus(OK)
    @ApiLogger(option = "user")
    @GetMapping(value = "verify", produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
    public String confirmEmail(@RequestParam("token") String token) {
        return confirmationTokenBusinessService.confirmToken(token);
    }
}
