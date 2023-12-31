package usw.suwiki.domain.timetable.controller;

import java.util.List;
import javax.validation.Valid;
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
import usw.suwiki.domain.timetable.dto.request.CreateTimetableCellRequest;
import usw.suwiki.domain.timetable.dto.request.CreateTimetableRequest;
import usw.suwiki.domain.timetable.dto.request.UpdateTimetableRequest;
import usw.suwiki.domain.timetable.dto.response.SimpleTimetableResponse;
import usw.suwiki.domain.timetable.dto.response.TimetableCellResponse;
import usw.suwiki.domain.timetable.dto.response.TimetableResponse;
import usw.suwiki.domain.timetable.service.TimetableService;
import usw.suwiki.global.dto.ApiResponse;
import usw.suwiki.global.dto.ResultResponse;
import usw.suwiki.global.jwt.JwtAgent;

@Slf4j
@RestController
@RequestMapping("/timetables/mine")// TODO: URI 패턴 변경 -> /timetables (시간표 리스트 조회 제외) 고민
@RequiredArgsConstructor
public class TimetableController {// TODO: PrincipalDetails 유저 인증 객체, AuthService 유저 검증 로직 추가
    private final TimetableService timetableService;
    private final JwtAgent jwtAgent;

    @PostMapping
    public ApiResponse<SimpleTimetableResponse> createTimetable(
            @RequestHeader String authorization,
            @Valid @RequestBody CreateTimetableRequest request // TODO: @Valid 및 Global Exception Handler 적용
    ) {
        jwtAgent.validateJwt(authorization);
        Long userId = jwtAgent.getId(authorization);

        return ApiResponse.success(timetableService.createTimetable(request, userId));
    }

    @PutMapping("/{timetableId}")
    public ApiResponse<SimpleTimetableResponse> updateTimetable(
            @PathVariable Long timetableId,
            @RequestHeader String authorization,
            @Valid @RequestBody UpdateTimetableRequest request // TODO: @Valid 및 Global Exception Handler 적용
    ) {
        jwtAgent.validateJwt(authorization);
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
    public ApiResponse<List<SimpleTimetableResponse>> getAllTimetableList(@RequestHeader String authorization) {
        jwtAgent.validateJwt(authorization);
        Long userId = jwtAgent.getId(authorization);

        return ApiResponse.success(timetableService.getAllTimetableList(userId));
    }

    // 시간표 상세 조회
    @GetMapping("/{timetableId}")
    public ApiResponse<TimetableResponse> getTimetable(
            @PathVariable Long timetableId,
            @RequestHeader String authorization
    ) {
        jwtAgent.validateJwt(authorization);

        return ApiResponse.success(timetableService.getTimetable(timetableId));
    }

    // 시간표 강의 - 생성
    @PostMapping("/{timetableId}/cells")
    public ApiResponse<TimetableCellResponse> createTimetableCell(
            @PathVariable Long timetableId,
            @RequestHeader String authorization,
            @Valid @RequestBody CreateTimetableCellRequest request
    ) {
        jwtAgent.validateJwt(authorization);
        Long userId = jwtAgent.getId(authorization);

        return ApiResponse.success(timetableService.createTimetableCell(request, timetableId, userId));
    }

    // 시간표 강의 - 수정

    // 시간표 강의 - 삭제

    // 시간표 일괄 DB 동기화 (시간표 및 강의 bulk 생성)
}
