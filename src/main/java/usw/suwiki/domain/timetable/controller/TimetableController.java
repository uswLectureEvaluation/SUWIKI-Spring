package usw.suwiki.domain.timetable.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.domain.timetable.dto.request.CreateTimetableRequest;
import usw.suwiki.domain.timetable.dto.response.CreateTimetableResponse;
import usw.suwiki.domain.timetable.service.TimetableService;
import usw.suwiki.global.dto.ApiResponse;
import usw.suwiki.global.jwt.JwtAgent;

@Slf4j
@RestController
@RequestMapping("/timetables/mine")
@RequiredArgsConstructor
public class TimetableController {// TODO: PrincipalDetails 유저 인증 객체, AuthService 유저 검증 로직 추가
    private final TimetableService timetableService;
    private final JwtAgent jwtAgent;

    @PostMapping
    public ApiResponse<CreateTimetableResponse> createTimetable(
            @RequestHeader String authorization,
            @RequestBody CreateTimetableRequest request
    ) {
        jwtAgent.validateJwt(authorization);    // TODO: V2 -> Jwt Filter 적용
        Long userId = jwtAgent.getId(authorization);

        return ApiResponse.success(timetableService.createTimetable(request, userId));
    }

    // 시간표 수정

    // 시간표 삭제

    // 시간표 리스트 조회

    // 시간표 상세 조회

    // 시간표 강의 - 생성

    // 시간표 강의 - 수정

    // 시간표 강의 - 삭제

    // 시간표 일괄 DB 동기화 (시간표 및 강의 bulk 생성)
}
