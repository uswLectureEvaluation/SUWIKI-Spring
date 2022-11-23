package usw.suwiki.domain.notice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.global.PageOption;
import usw.suwiki.global.ToJsonArray;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.jwt.JwtTokenValidator;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "https://suwikiman.netlify.app/", allowedHeaders = "*")
@RequestMapping(value = "/notice")
public class NoticeController {

    private final NoticeService noticeService;
    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenResolver jwtTokenResolver;

    @GetMapping("/all")
    public ResponseEntity<ToJsonArray> findNoticeList(@RequestParam(required = false) Optional<Integer> page) {
        HttpHeaders header = new HttpHeaders();
        List<NoticeResponseDto> list = noticeService.findNoticeList(new PageOption(page));
        ToJsonArray data = new ToJsonArray(list);
        return new ResponseEntity<ToJsonArray>(data, header, HttpStatus.valueOf(200));
    }

    @GetMapping("/")
    public ResponseEntity<ToJsonArray> findNoticeByNoticeId(@RequestParam Long noticeId) {
        HttpHeaders header = new HttpHeaders();
        NoticeDetailResponseDto dto = noticeService.findNoticeDetail(noticeId);
        ToJsonArray data = new ToJsonArray(dto);
        return new ResponseEntity<ToJsonArray>(data, header, HttpStatus.valueOf(200));
    }

    @PostMapping("/")
    public ResponseEntity<String> saveNotice(@RequestBody NoticeSaveOrUpdateDto dto, @RequestHeader String Authorization) {
        HttpHeaders header = new HttpHeaders();
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserRole(Authorization).equals("ADMIN")) {
                noticeService.save(dto);
                return new ResponseEntity<String>("success", header, HttpStatus.valueOf(200));
            } else {
                throw new AccountException(ErrorType.USER_RESTRICTED);
            }
        } else {
            throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
        }
    }

    @PutMapping("/")
    public ResponseEntity<String> updateNotice(@RequestParam Long noticeId, @RequestBody NoticeSaveOrUpdateDto dto, @RequestHeader String Authorization) {
        HttpHeaders header = new HttpHeaders();
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserRole(Authorization).equals("ADMIN")) {
                noticeService.update(dto, noticeId);
                return new ResponseEntity<String>("success", header, HttpStatus.valueOf(200));
            } else {
                throw new AccountException(ErrorType.USER_RESTRICTED);
            }
        } else {
            throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
        }
    }

    @DeleteMapping("/")
    public ResponseEntity<String> deleteNotice(@RequestParam Long noticeId, @RequestHeader String Authorization) {
        HttpHeaders header = new HttpHeaders();
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserRole(Authorization).equals("ADMIN")) {
                noticeService.delete(noticeId);
                return new ResponseEntity<String>("success", header, HttpStatus.valueOf(200));
            } else {
                throw new AccountException(ErrorType.USER_RESTRICTED);
            }
        } else {
            throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
        }
    }
}


