package usw.suwiki.domain.version.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.domain.version.dto.response.CheckUpdateMandatoryResponse;
import usw.suwiki.domain.version.service.ClientAppVersionService;

@Slf4j
@RestController
@RequestMapping("/client/version")
@RequiredArgsConstructor
public class ClientAppVersionController {
    private final ClientAppVersionService clientAppVersionService;

    @GetMapping("/update-mandatory")
    public ResponseEntity<CheckUpdateMandatoryResponse> checkIsUpdateMandatory(
            @RequestParam String os,
            @RequestParam Integer versionCode
    ) {
        return ResponseEntity.ok(clientAppVersionService.checkIsUpdateMandatory(os, versionCode));
    }
}
