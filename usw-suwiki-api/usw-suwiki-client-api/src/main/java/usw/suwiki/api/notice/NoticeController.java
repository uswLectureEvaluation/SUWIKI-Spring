package usw.suwiki.api.notice;

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
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.core.jwt.JwtAgent;
import usw.suwiki.common.pagination.PageOption;
import usw.suwiki.common.response.ResponseForm;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.domain.notice.dto.NoticeDetailResponseDto;
import usw.suwiki.domain.notice.dto.NoticeResponseDto;
import usw.suwiki.domain.notice.dto.NoticeSaveOrUpdateDto;
import usw.suwiki.domain.notice.service.NoticeService;
import usw.suwiki.statistics.annotation.ApiLogger;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/notice")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;
    private final JwtAgent jwtAgent;

    @ApiLogger(option = "notice")
    @GetMapping("/all")
    public ResponseForm findNoticesApi(@RequestParam(required = false) Optional<Integer> page) {
        PageOption option = new PageOption(page);
        List<NoticeResponseDto> response = noticeService.readAllNotice(option);
        return new ResponseForm(response);
    }

    @ApiLogger(option = "notice")
    @GetMapping("/")
    public ResponseForm findNoticeApi(@RequestParam Long noticeId) {
        NoticeDetailResponseDto response = noticeService.readNotice(noticeId);
        return new ResponseForm(response);
    }

    @ApiLogger(option = "notice")
    @GetMapping("/v2/all")
    public ResponseForm findNoticesApiV2(@RequestParam(required = false) Optional<Integer> page) {
        PageOption option = new PageOption(page);
        List<NoticeResponseDto> response = noticeService.readAllNotice(option);
        return new ResponseForm(response);
    }

    @ApiLogger(option = "notice")
    @GetMapping("/v2/")
    public ResponseForm findNoticeApiV2(@RequestParam Long noticeId) {
        NoticeDetailResponseDto response = noticeService.readNotice(noticeId);
        return new ResponseForm(response);
    }

    @ApiLogger(option = "notice")
    @PostMapping("/")
    public String writeNoticeApi(@RequestBody NoticeSaveOrUpdateDto request, @RequestHeader String Authorization) {
        jwtAgent.validateJwt(Authorization);
        validateAdmin(Authorization);
        noticeService.write(request);

        return "success";
    }

    @ApiLogger(option = "notice")
    @PutMapping("/")
    public String updateNotice(
        @RequestParam Long noticeId,
        @RequestBody NoticeSaveOrUpdateDto dto,
        @RequestHeader String Authorization
    ) {
        jwtAgent.validateJwt(Authorization);
        validateAdmin(Authorization);
        noticeService.update(dto, noticeId);

        return "success";
    }

    @ApiLogger(option = "notice")
    @DeleteMapping("/")
    public String deleteNotice(@RequestParam Long noticeId, @RequestHeader String Authorization) {
        jwtAgent.validateJwt(Authorization);
        validateAdmin(Authorization);
        noticeService.delete(noticeId);

        return "success";
    }

    private void validateAdmin(String authorization) {
        if (!(jwtAgent.getUserRole(authorization).equals("ADMIN"))) {
            throw new AccountException(ExceptionType.USER_RESTRICTED);
        }
    }
}


