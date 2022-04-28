package usw.suwiki.controller.exam;

import org.springframework.http.MediaType;
import usw.suwiki.dto.PageOption;
import usw.suwiki.dto.ToJsonArray;
import usw.suwiki.dto.exam_info.*;
import usw.suwiki.dto.lecture.LectureToJsonArray;
import usw.suwiki.dto.view_exam.PurchaseHistoryDto;
import usw.suwiki.exception.AccountException;
import usw.suwiki.exception.ErrorType;
import usw.suwiki.jwt.JwtTokenResolver;
import usw.suwiki.jwt.JwtTokenValidator;
import usw.suwiki.service.exam.ExamPostsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.service.viewExam.ViewExamService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/exam-posts")
public class ExamPostsController {

    private final ExamPostsService examPostsService;
    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenResolver jwtTokenResolver;
    private final ViewExamService viewExamService;

    @GetMapping("/findByLectureId") // 이름 수정
    public ResponseEntity<FindByLectureToExam> findByLecture(@RequestParam Long lectureId, @RequestHeader String Authorization,
                                                   @RequestParam(required = false) Optional<Integer> page){
        HttpHeaders header = new HttpHeaders();
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization)) throw new AccountException(ErrorType.USER_RESTRICTED);
            List<ExamResponseByLectureIdDto> list = examPostsService.findExamPostsByLectureId(new PageOption(page), lectureId);
            FindByLectureToExam data = new FindByLectureToExam(list);
            if(examPostsService.verifyWriteExamPosts(jwtTokenResolver.getId(Authorization), lectureId)){
                data.setWritten(false);
            }
            if(list.isEmpty()){
                data.setExamDataExist(false);
                return new ResponseEntity<FindByLectureToExam>(data, header, HttpStatus.valueOf(200));
            } else {
                if(viewExamService.verifyAuth(lectureId, jwtTokenResolver.getId(Authorization))) {
                    return new ResponseEntity<FindByLectureToExam>(data, header, HttpStatus.valueOf(200));
                }else{
                    data.setData(new ArrayList<>());
                    return new ResponseEntity<FindByLectureToExam>(data, header, HttpStatus.valueOf(200));
                }
            }
        }else throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
    }

    @PostMapping("/buyExamInfo")
    public ResponseEntity<String> buyExamInfo(@RequestParam Long lectureId,@RequestHeader String Authorization){
        HttpHeaders header = new HttpHeaders();
        if (jwtTokenValidator.validateAccessToken(Authorization)){
            if (jwtTokenResolver.getUserIsRestricted(Authorization)) throw new AccountException(ErrorType.USER_RESTRICTED);
            viewExamService.save(lectureId, jwtTokenResolver.getId(Authorization));
            return new ResponseEntity<String>("success", header, HttpStatus.valueOf(200));
        }else throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
    }

    @PostMapping("/write")
    public ResponseEntity<String> saveEvaluatePosts(@RequestParam Long lectureId ,@RequestBody ExamPostsSaveDto dto, @RequestHeader String Authorization){
        HttpHeaders header = new HttpHeaders();
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization)) throw new AccountException(ErrorType.USER_RESTRICTED);
            Long userIdx = jwtTokenResolver.getId(Authorization);
            if (examPostsService.verifyWriteExamPosts(userIdx, lectureId)) {
                examPostsService.save(dto, userIdx,lectureId);
                return new ResponseEntity<String>("success", header, HttpStatus.valueOf(200));
            }else{
                throw new AccountException(ErrorType.POSTS_WRITE_OVERLAP);
            }
        }else throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateExamPosts(@RequestParam Long examIdx, @RequestHeader String Authorization, @RequestBody ExamPostsUpdateDto dto){
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization)) throw new AccountException(ErrorType.USER_RESTRICTED);
            examPostsService.update(examIdx,dto);
            return new ResponseEntity<String>("success", header, HttpStatus.valueOf(200));
        }else throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
    }

    @GetMapping("/findByUserIdx") // 이름 수정 , 널값 처리 프론트
    public ResponseEntity<ToJsonArray> findByUser(@RequestHeader String Authorization,
                                                         @RequestParam(required = false) Optional<Integer> page){
        HttpHeaders header = new HttpHeaders();
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization)) throw new AccountException(ErrorType.USER_RESTRICTED);
            List<ExamResponseByUserIdxDto> list = examPostsService.findExamPostsByUserId(new PageOption(page),
                    jwtTokenResolver.getId(Authorization));

            ToJsonArray data = new ToJsonArray(list);
                return new ResponseEntity<ToJsonArray>(data, header, HttpStatus.valueOf(200));

        }else throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteExamPosts(@RequestParam Long examIdx,@RequestHeader String Authorization){
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            if (jwtTokenResolver.getUserIsRestricted(Authorization)) throw new AccountException(ErrorType.USER_RESTRICTED);
            Long userIdx = jwtTokenResolver.getId(Authorization);
            if (examPostsService.verifyDeleteExamPosts(userIdx, examIdx)) {
                examPostsService.deleteById(examIdx,userIdx);
                return new ResponseEntity<String>("success", header, HttpStatus.valueOf(200));
            }else{
                throw new AccountException(ErrorType.USER_POINT_LACK);
            }
        } else throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
    }

    @GetMapping("/purchase-history") // 이름 수정 , 널값 처리 프론트
    public ResponseEntity<ToJsonArray> showPurchaseHistory(@RequestHeader String Authorization){
        HttpHeaders header = new HttpHeaders();
        if (jwtTokenValidator.validateAccessToken(Authorization)) {
            Long userIdx = jwtTokenResolver.getId(Authorization);
            List<PurchaseHistoryDto> list = viewExamService.findByUserId(userIdx);
            ToJsonArray data = new ToJsonArray(list);
            return new ResponseEntity<ToJsonArray>(data, header, HttpStatus.valueOf(200));

        }else throw new AccountException(ErrorType.TOKEN_IS_NOT_FOUND);
    }
}
