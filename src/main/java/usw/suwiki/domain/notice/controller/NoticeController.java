package usw.suwiki.domain.notice.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import usw.suwiki.domain.notice.controller.dto.NoticeDetailResponseDto;
import usw.suwiki.domain.notice.controller.dto.NoticeResponseDto;
import usw.suwiki.domain.notice.controller.dto.NoticeSaveOrUpdateDto;
import usw.suwiki.domain.notice.service.NoticeService;
import usw.suwiki.global.PageOption;
import usw.suwiki.global.ResponseForm;
import usw.suwiki.global.annotation.ApiLogger;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtAgent;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "/notice")
public class NoticeController {

	private final NoticeService noticeService;
	private final JwtAgent jwtAgent;

	@ApiLogger(option = "notice")
	@GetMapping("/all")
	public ResponseForm findNoticesApi(
		@RequestParam(required = false) Optional<Integer> page) {

		PageOption option = new PageOption(page);
		List<NoticeResponseDto> response = noticeService.readAllNotice(option);
		return new ResponseForm(response);
	}

	@ApiLogger(option = "notice")
	@GetMapping("/")
	public ResponseForm findNoticeApi(
		@RequestParam Long noticeId) {

		NoticeDetailResponseDto response = noticeService.readNotice(noticeId);
		return new ResponseForm(response);
	}

	@ApiLogger(option = "notice")
	@PostMapping("/")
	public String writeNoticeApi(
		@RequestBody NoticeSaveOrUpdateDto requestBody,
		@RequestHeader String Authorization) {

		jwtAgent.validateJwt(Authorization);
		validateAdmin(Authorization);
		noticeService.write(requestBody);

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
	public String deleteNotice(@RequestParam Long noticeId,
		@RequestHeader String Authorization) {
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


