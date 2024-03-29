package usw.suwiki.domain.lecture.controller;

import static usw.suwiki.global.exception.ExceptionType.USER_RESTRICTED;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.domain.lecture.controller.dto.LectureAndCountResponseForm;
import usw.suwiki.domain.lecture.controller.dto.LectureDetailResponseDto;
import usw.suwiki.domain.lecture.controller.dto.LectureFindOption;
import usw.suwiki.domain.lecture.controller.dto.LectureWithOptionalScheduleResponse;
import usw.suwiki.domain.lecture.service.LectureService;
import usw.suwiki.global.ResponseForm;
import usw.suwiki.global.annotation.ApiLogger;
import usw.suwiki.global.annotation.CacheStatics;
import usw.suwiki.global.dto.ApiResponse;
import usw.suwiki.global.dto.NoOffsetPaginationResponse;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtAgent;


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
        LectureAndCountResponseForm response = lectureService.readLectureByKeyword(
            searchValue, findOption);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/current/cells/search")
    public ResponseEntity<ApiResponse<NoOffsetPaginationResponse<LectureWithOptionalScheduleResponse>>> searchLectureCells(
        @RequestParam(required = false) Long cursorId,
        @RequestParam(required = false, defaultValue = "20") Integer size,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String major,
        @RequestParam(required = false) Integer grade
    ) {
        NoOffsetPaginationResponse<LectureWithOptionalScheduleResponse> response =
            lectureService.findPagedLecturesWithSchedule(cursorId, size, keyword, major, grade);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Cacheable(cacheNames = "lecture")
    @ApiLogger(option = "lecture")
    @CacheStatics
    @GetMapping("/all")
    public ResponseEntity<LectureAndCountResponseForm> findAllLectureApi(
        @RequestParam(required = false) String option,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) String majorType
    ) {

        LectureFindOption findOption = new LectureFindOption(option, page, majorType);
        LectureAndCountResponseForm response = lectureService.readAllLecture(findOption);
        return ResponseEntity.ok(response);
    }

    @ApiLogger(option = "lecture")
    @GetMapping
    public ResponseEntity<ResponseForm> findLectureByLectureId(
        @RequestParam Long lectureId,
        @RequestHeader String Authorization
    ) {

        if (jwtAgent.getUserIsRestricted(Authorization)) {
            throw new AccountException(USER_RESTRICTED);
        }
        LectureDetailResponseDto lecture = lectureService.readLectureDetail(lectureId);
        ResponseForm response = new ResponseForm(lecture);
        return ResponseEntity.ok(response);
    }
}
