package usw.suwiki.controller.lecture;

import org.springframework.web.bind.annotation.*;
import usw.suwiki.dto.ToJsonArray;
import usw.suwiki.dto.lecture.LectureDetailResponseDto;
import usw.suwiki.dto.lecture.LectureFindOption;
import usw.suwiki.dto.lecture.LectureResponseDto;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.jwt.JwtTokenResolver;
import usw.suwiki.jwt.JwtTokenValidator;
import usw.suwiki.service.lecture.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import usw.suwiki.service.user.UserService;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/lecture")
public class LectureController {
    private final LectureService lectureService;
    private final JwtTokenValidator jwtTokenValidator;
    private final UserService userService;
    private final JwtTokenResolver jwtTokenResolver;

    @GetMapping("/findBySearchValue")
    public ResponseEntity<ToJsonArray>findByLectureList(@RequestParam String searchValue, @RequestParam(required = false)
            Optional<String> option, @RequestParam(required = false) Optional<Integer> page){
        HttpHeaders header = new HttpHeaders();

        if(searchValue.equals(null)){
            throw new AccountException(ErrorType.NOT_EXISTS_LECTURE_NAME);
        }

        List<LectureResponseDto> list = lectureService.findLectureByFindOption
                (searchValue,new LectureFindOption(option,page));

        ToJsonArray data = new ToJsonArray(list);

        return new ResponseEntity<ToJsonArray>(data, header, HttpStatus.valueOf(200));
    }

    @GetMapping("/findAllList")
    public ResponseEntity<ToJsonArray>findAllList(@RequestParam(required = false) Optional<String> option,
                                                           @RequestParam(required = false) Optional<Integer> page){
        HttpHeaders header = new HttpHeaders();

        List<LectureResponseDto> list = lectureService.findAllLectureByFindOption
                (new LectureFindOption(option,page));

        ToJsonArray data = new ToJsonArray(list);

        return new ResponseEntity<ToJsonArray>(data, header, HttpStatus.valueOf(200));
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
