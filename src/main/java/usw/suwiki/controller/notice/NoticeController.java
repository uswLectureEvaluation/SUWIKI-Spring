package usw.suwiki.controller.notice;

import usw.suwiki.dto.PageOption;
import usw.suwiki.dto.ToJsonArray;
import usw.suwiki.dto.notice.NoticeDetailResponseDto;
import usw.suwiki.dto.notice.NoticeResponseDto;
import usw.suwiki.dto.notice.NoticeSaveOrUpdateDto;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.jwt.JwtTokenResolver;
import usw.suwiki.jwt.JwtTokenValidator;
import usw.suwiki.service.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/notice")
public class NoticeController {

    private final NoticeService noticeService;
    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenResolver jwtTokenResolver;

    @GetMapping("/all")
    public ResponseEntity<ToJsonArray> findNoticeList(@RequestParam(required = false) Optional<Integer> page){
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
    public ResponseEntity<String> saveNotice(@RequestBody NoticeSaveOrUpdateDto dto, @RequestHeader String Authorization){
        HttpHeaders header = new HttpHeaders();
            if(jwtTokenValidator.validateAccessToken(Authorization)) {
//                if (jwtTokenResolver.getUserRole(Authorization).equals("ADMIN")) {
                    noticeService.save(dto);
                    return new ResponseEntity<String>("success", header, HttpStatus.valueOf(200));
//                } else {
//                    throw new AccountException(ErrorType.USER_RESTRICTED);
//                }
            } else {
                throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
            }
    }

    @PutMapping("/")
    public ResponseEntity<String> updateNotice(@RequestParam Long noticeId , @RequestBody NoticeSaveOrUpdateDto dto, @RequestHeader String Authorization){
        HttpHeaders header = new HttpHeaders();
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
//            if (jwtTokenResolver.getUserRole(Authorization).equals("ADMIN")) {
                noticeService.update(dto, noticeId);
                return new ResponseEntity<String>("success", header, HttpStatus.valueOf(200));
//            } else {
//                throw new AccountException(ErrorType.USER_RESTRICTED);
//            }
        } else {
            throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteNotice(@RequestParam Long noticeId , @RequestHeader String Authorization){
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


