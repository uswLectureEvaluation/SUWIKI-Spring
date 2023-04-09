package usw.suwiki.domain.lecture.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import usw.suwiki.domain.lecture.controller.dto.LectureDetailResponseDto;
import usw.suwiki.domain.lecture.controller.dto.LectureAndCountResponseForm;
import usw.suwiki.domain.lecture.controller.dto.LectureFindOption;
import usw.suwiki.domain.lecture.service.LectureService;
import usw.suwiki.global.ResponseForm;
import usw.suwiki.global.annotation.ApiLogger;
import usw.suwiki.global.exception.ErrorType;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.jwt.JwtTokenValidator;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/lecture")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LectureController {

    private final LectureService lectureService;
    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenResolver jwtTokenResolver;

    @ApiLogger(option = "lecture")
    @GetMapping("/search")
    public ResponseEntity<LectureAndCountResponseForm> searchLectureApi(
        @RequestParam String searchValue,
        @RequestParam(required = false) String option,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) String majorType) {

        LectureFindOption findOption = new LectureFindOption(option, page, majorType);
        LectureAndCountResponseForm response = lectureService.findLectureByKeyword(searchValue, findOption);

        return ResponseEntity.ok(response);
    }

    @ApiLogger(option = "lecture")
    @GetMapping("/all")
    public ResponseEntity<LectureAndCountResponseForm>findAllLectureApi(
        @RequestParam(required = false) String option,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) String majorType){

        LectureFindOption findOption = new LectureFindOption(option, page, majorType);
        LectureAndCountResponseForm response = lectureService.findAllLecture(findOption);
        return ResponseEntity.ok(response);
    }

    @ApiLogger(option = "lecture")
    @GetMapping
    public ResponseEntity<ResponseForm> findLectureByLectureId(
        @RequestParam Long lectureId,
        @RequestHeader String Authorization) {

        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization))
                throw new AccountException(ErrorType.USER_RESTRICTED);
            LectureDetailResponseDto lecture = lectureService.findByIdDetail(lectureId);
            ResponseForm response = new ResponseForm(lecture);
            return ResponseEntity.ok(response);
        }
        throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
    }

}
