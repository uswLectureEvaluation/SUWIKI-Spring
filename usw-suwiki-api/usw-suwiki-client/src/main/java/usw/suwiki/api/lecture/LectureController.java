package usw.suwiki.api.lecture;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.common.response.ApiResponse;
import usw.suwiki.common.response.NoOffsetPaginationResponse;
import usw.suwiki.common.response.ResponseForm;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.domain.lecture.dto.LectureAndCountResponseForm;
import usw.suwiki.domain.lecture.dto.LectureDetailResponseDto;
import usw.suwiki.domain.lecture.dto.LectureFindOption;
import usw.suwiki.domain.lecture.dto.LectureWithOptionalScheduleResponse;
import usw.suwiki.domain.lecture.service.LectureService;
import usw.suwiki.global.jwt.JwtAgent;
import usw.suwiki.statistics.annotation.ApiLogger;
import usw.suwiki.statistics.annotation.CacheStatics;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/lecture")
@RequiredArgsConstructor
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

    @CacheStatics
    @Cacheable(cacheNames = "lecture")
    @ApiLogger(option = "lecture")
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
            throw new AccountException(ExceptionType.USER_RESTRICTED);
        }

        LectureDetailResponseDto lecture = lectureService.readLectureDetail(lectureId);
        return ResponseEntity.ok(new ResponseForm(lecture));
    }
}
