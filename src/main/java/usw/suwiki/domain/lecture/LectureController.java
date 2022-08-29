package usw.suwiki.domain.lecture;

import org.springframework.web.bind.annotation.*;
import usw.suwiki.global.ToJsonArray;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "//lecture")
public class LectureController {
    private final LectureService lectureService;
    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenResolver jwtTokenResolver;

    @GetMapping("/search")
    public ResponseEntity<LectureToJsonArray>findByLectureSearchValue(@RequestParam String searchValue, @RequestParam(required = false)
            Optional<String> option, @RequestParam(required = false) Optional<Integer> page, @RequestParam(required = false) Optional<String> majorType){
        HttpHeaders header = new HttpHeaders();
        LectureFindOption findOption = LectureFindOption.builder().orderOption(option).pageNumber(page).majorType(majorType).build();
        if(findOption.getMajorType().get().equals("")){
            LectureToJsonArray  data = lectureService.findLectureByFindOption(searchValue, findOption);
            return new ResponseEntity<LectureToJsonArray>(data, header, HttpStatus.valueOf(200));
        }else {
            LectureToJsonArray data = lectureService.findLectureByMajorType(searchValue, findOption);
            return new ResponseEntity<LectureToJsonArray>(data, header, HttpStatus.valueOf(200));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<LectureToJsonArray>findAllList(@RequestParam(required = false) Optional<String> option,
                                                           @RequestParam(required = false) Optional<Integer> page,
                                                         @RequestParam(required = false) Optional<String> majorType){
        HttpHeaders header = new HttpHeaders();

        LectureFindOption findOption = LectureFindOption.builder().orderOption(option).pageNumber(page).majorType(majorType).build();
        if(findOption.getMajorType().get().equals("")){
            LectureToJsonArray  data = lectureService.findAllLectureByFindOption(findOption);
            return new ResponseEntity<LectureToJsonArray>(data, header, HttpStatus.valueOf(200));
        }else {
            LectureToJsonArray data = lectureService.findAllLectureByMajorType(findOption);
            return new ResponseEntity<LectureToJsonArray>(data, header, HttpStatus.valueOf(200));
        }
    }

    @GetMapping
    public ResponseEntity<ToJsonArray>findLectureByLectureId(@RequestParam Long lectureId ,@RequestHeader String Authorization){
        HttpHeaders header = new HttpHeaders();

        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization)) throw new AccountException(ErrorType.USER_RESTRICTED);
            LectureDetailResponseDto lecture = lectureService.findByIdDetail(lectureId);
            ToJsonArray data = new ToJsonArray(lecture);
            return new ResponseEntity<ToJsonArray>(data, header, HttpStatus.valueOf(200));
        }else throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);

    }

}
