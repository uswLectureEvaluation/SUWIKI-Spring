package usw.suwiki.domain.timetable.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.domain.timetable.dto.request.CreateTimetableRequest;
import usw.suwiki.domain.timetable.dto.request.UpdateTimetableRequest;
import usw.suwiki.domain.timetable.dto.response.TimetableResponse;
import usw.suwiki.domain.timetable.service.TimetableService;
import usw.suwiki.global.dto.ApiResponse;
import usw.suwiki.global.dto.ResultResponse;
import usw.suwiki.global.jwt.JwtAgent;

@Slf4j
@RestController
@RequestMapping("/timetables/mine")
@RequiredArgsConstructor
public class TimetableController {// TODO: PrincipalDetails 유저 인증 객체, AuthService 유저 검증 로직 추가
    private final TimetableService timetableService;
    private final JwtAgent jwtAgent;

    @PostMapping
    public ApiResponse<TimetableResponse> createTimetable(
            @RequestHeader String authorization,
            @RequestBody CreateTimetableRequest request
    ) {
        jwtAgent.validateJwt(authorization);    // TODO: V2 -> Jwt Filter 적용
        Long userId = jwtAgent.getId(authorization);

        return ApiResponse.success(timetableService.createTimetable(request, userId));
    }

    @PutMapping("/{timetableId}")
    public ApiResponse<TimetableResponse> updateTimetable(
            @PathVariable Long timetableId,
            @RequestHeader String authorization,
            @RequestBody UpdateTimetableRequest request
    ) {
        jwtAgent.validateJwt(authorization);    // TODO: V2 -> Jwt Filter 적용
        Long userId = jwtAgent.getId(authorization);

        return ApiResponse.success(timetableService.updateTimetable(request, timetableId, userId));
    }

    // 시간표 삭제
    @DeleteMapping("/{timetableId}")
    public ApiResponse<ResultResponse> deleteTimetable(
            @PathVariable Long timetableId,
            @RequestHeader String authorization
    ) {
        jwtAgent.validateJwt(authorization);
        Long userId = jwtAgent.getId(authorization);

        return ApiResponse.success(timetableService.deleteTimetable(timetableId, userId));
    }

    // 시간표 리스트 조회
    @GetMapping("/all")
    public ApiResponse<List<TimetableResponse>> getAllTimetableList(@RequestHeader String authorization) {
        jwtAgent.validateJwt(authorization);
        Long userId = jwtAgent.getId(authorization);

        return ApiResponse.success(timetableService.getAllTimetableList(userId));
    }

    // 시간표 상세 조회

    // 시간표 강의 - 생성

    // 시간표 강의 - 수정

    // 시간표 강의 - 삭제

    // 시간표 일괄 DB 동기화 (시간표 및 강의 bulk 생성)
}
