package usw.suwiki.domain.lecture.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.lecture.controller.dto.LectureAndCountResponseForm;
import usw.suwiki.domain.lecture.controller.dto.LectureDetailResponseDto;
import usw.suwiki.domain.lecture.controller.dto.LectureFindOption;
import usw.suwiki.domain.lecture.service.LectureService;
import usw.suwiki.global.ResponseForm;
import usw.suwiki.global.annotation.ApiLogger;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtAgent;

import static usw.suwiki.global.exception.ExceptionType.USER_RESTRICTED;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/lecture")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LectureController {

    private final LectureService lectureService;
    private final JwtAgent jwtAgent;

    @ApiLogger(option = "lecture")
    @GetMapping("/search")
    public ResponseEntity<LectureAndCountResponseForm> searchLectureApi(
            @RequestParam String searchValue,
            @RequestParam(required = false) String option,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) String majorType
    ) {
        LectureFindOption findOption = new LectureFindOption(option, page, majorType);
        LectureAndCountResponseForm response = lectureService.findLectureByKeyword(
                searchValue, findOption);

        return ResponseEntity.ok(response);
    }

    @ApiLogger(option = "lecture")
    @GetMapping("/all")
    public ResponseEntity<LectureAndCountResponseForm> findAllLectureApi(
            @RequestParam(required = false) String option,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) String majorType) {

        LectureFindOption findOption = new LectureFindOption(option, page, majorType);
        LectureAndCountResponseForm response = lectureService.findAllLecture(findOption);
        return ResponseEntity.ok(response);
    }

    @ApiLogger(option = "lecture")
    @GetMapping
    public ResponseEntity<ResponseForm> findLectureByLectureId(
            @RequestParam Long lectureId,
            @RequestHeader String Authorization) {

        if (jwtAgent.getUserIsRestricted(Authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }
        LectureDetailResponseDto lecture = lectureService.findByIdDetail(lectureId);
        ResponseForm response = new ResponseForm(lecture);
        return ResponseEntity.ok(response);
    }
}
