package usw.suwiki.domain.notice.controller;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import usw.suwiki.domain.notice.dto.NoticeDetailResponseDto;
import usw.suwiki.domain.notice.dto.NoticeResponseDto;
import usw.suwiki.domain.notice.dto.NoticeSaveOrUpdateDto;
import usw.suwiki.domain.notice.service.NoticeService;
import usw.suwiki.global.PageOption;
import usw.suwiki.global.ResponseForm;
import usw.suwiki.global.annotation.ApiLogger;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtResolver;
import usw.suwiki.global.jwt.JwtValidator;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "/notice")
public class NoticeController {

    private final NoticeService noticeService;
    private final JwtValidator jwtValidator;
    private final JwtResolver jwtResolver;

    @ApiLogger(option = "notice")
    @GetMapping("/all")
    public ResponseEntity<ResponseForm> findNoticeList(
        @RequestParam(required = false) Optional<Integer> page
    ) {
        HttpHeaders header = new HttpHeaders();
        List<NoticeResponseDto> list = noticeService.findNoticeList(new PageOption(page));
        ResponseForm data = new ResponseForm(list);
        return new ResponseEntity<>(data, header, HttpStatus.valueOf(200));
    }

    @ApiLogger(option = "notice")
    @GetMapping("/")
    public ResponseEntity<ResponseForm> findNoticeByNoticeId(
        @RequestParam Long noticeId
    ) {
        HttpHeaders header = new HttpHeaders();
        NoticeDetailResponseDto dto = noticeService.findNoticeDetail(noticeId);
        ResponseForm data = new ResponseForm(dto);
        return new ResponseEntity<>(data, header, HttpStatus.valueOf(200));
    }

    @ApiLogger(option = "notice")
    @PostMapping("/")
    public ResponseEntity<String> saveNotice(
        @RequestBody NoticeSaveOrUpdateDto dto,
        @RequestHeader String Authorization
    ) {
        HttpHeaders header = new HttpHeaders();
        jwtValidator.validateJwt(Authorization);
        if (jwtResolver.getUserRole(Authorization).equals("ADMIN")) {
            noticeService.save(dto);
            return new ResponseEntity<>("success", header, HttpStatus.valueOf(200));
        } else {
            throw new AccountException(ExceptionType.USER_RESTRICTED);
        }
    }

    @ApiLogger(option = "notice")
    @PutMapping("/")
    public ResponseEntity<String> updateNotice(
        @RequestParam Long noticeId,
        @RequestBody NoticeSaveOrUpdateDto dto,
        @RequestHeader String Authorization
    ) {
        HttpHeaders header = new HttpHeaders();
        jwtValidator.validateJwt(Authorization);
        if (jwtResolver.getUserRole(Authorization).equals("ADMIN")) {
            noticeService.update(dto, noticeId);
            return new ResponseEntity<>("success", header, HttpStatus.valueOf(200));
        } else {
            throw new AccountException(ExceptionType.USER_RESTRICTED);
        }
    }

    @ApiLogger(option = "notice")
    @DeleteMapping("/")
    public ResponseEntity<String> deleteNotice(@RequestParam Long noticeId,
        @RequestHeader String Authorization) {
        HttpHeaders header = new HttpHeaders();
        jwtValidator.validateJwt(Authorization);
        if (jwtResolver.getUserRole(Authorization).equals("ADMIN")) {
            noticeService.delete(noticeId);
            return new ResponseEntity<>("success", header, HttpStatus.valueOf(200));
        } else {
            throw new AccountException(ExceptionType.USER_RESTRICTED);
        }
    }
}


