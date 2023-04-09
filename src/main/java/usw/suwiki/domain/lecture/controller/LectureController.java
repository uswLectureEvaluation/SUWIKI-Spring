package usw.suwiki.domain.lecture.controller;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.domain.lecture.LectureFindOption;
import usw.suwiki.domain.lecture.LectureToJsonArray;
import usw.suwiki.domain.lecture.dto.LectureDetailResponseDto;
import usw.suwiki.domain.lecture.service.LectureService;
import usw.suwiki.global.ToJsonArray;
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
    public ResponseEntity<LectureToJsonArray> searchLectureApi(
        @RequestParam String searchValue,
        @RequestParam(required = false) Optional<String> option,
        @RequestParam(required = false) Optional<Integer> page,
        @RequestParam(required = false) Optional<String> majorType) {

        LectureToJsonArray response = lectureService.searchLecture(searchValue, option, page, majorType);
        return ResponseEntity.ok(response);
    }

    @ApiLogger(option = "lecture")
    @GetMapping("/all")
    public ResponseEntity<LectureToJsonArray>findAllLectureApi(
        @RequestParam(required = false) Optional<String> option,
        @RequestParam(required = false) Optional<Integer> page,
        @RequestParam(required = false) Optional<String> majorType){

        LectureToJsonArray response = lectureService.findAllLecture(option, page, majorType);
        return ResponseEntity.ok(response);
    }

    @ApiLogger(option = "lecture")
    @GetMapping
    public ResponseEntity<ToJsonArray>findLectureByLectureId(@RequestParam Long lectureId ,@RequestHeader String Authorization){
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization)) throw new AccountException(ErrorType.USER_RESTRICTED);
            LectureDetailResponseDto lecture = lectureService.findByIdDetail(lectureId);
            ToJsonArray response = new ToJsonArray(lecture);
            return ResponseEntity.ok(response);
        }
        throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
    }

}
