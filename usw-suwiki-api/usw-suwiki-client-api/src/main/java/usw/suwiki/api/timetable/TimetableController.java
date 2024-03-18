package usw.suwiki.api.timetable;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.core.jwt.JwtAgent;
import usw.suwiki.common.request.BulkRequest;
import usw.suwiki.common.response.ApiResponse;
import usw.suwiki.common.response.ResultResponse;
import usw.suwiki.domain.lecture.timetable.service.TimetableService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/timetables")
@RequiredArgsConstructor
public class TimetableController {// TODO: PrincipalDetails 유저 인증 객체, AuthService 유저 검증 로직 추가
  private final TimetableService timetableService;
  private final JwtAgent jwtAgent;

  @PostMapping
  public ApiResponse<SimpleTimetableResponse> createTimetable(
    @RequestHeader String authorization,
    @Valid @RequestBody CreateTimetableRequest request
  ) {
    jwtAgent.validateJwt(authorization);
    Long userId = jwtAgent.getId(authorization);

    return ApiResponse.success(timetableService.createTimetable(request, userId));
  }

  @PostMapping("/bulk")
  public ApiResponse<List<TimetableResponse>> bulkCreateTimetables(
    @RequestHeader String authorization,
    @Valid @RequestBody BulkRequest<CreateWholeTimetableRequest> request
  ) {
    jwtAgent.validateJwt(authorization);
    Long userId = jwtAgent.getId(authorization);

    return ApiResponse.success(timetableService.bulkCreateTimetables(request.getBulk(), userId));
  }

  @PutMapping("/{timetableId}")
  public ApiResponse<SimpleTimetableResponse> updateTimetable(
    @PathVariable Long timetableId,
    @RequestHeader String authorization,
    @Valid @RequestBody UpdateTimetableRequest request
  ) {
    jwtAgent.validateJwt(authorization);
    Long userId = jwtAgent.getId(authorization);

    return ApiResponse.success(timetableService.updateTimetable(request, timetableId, userId));
  }

  @DeleteMapping("/{timetableId}")
  public ApiResponse<ResultResponse> deleteTimetable(@PathVariable Long timetableId, @RequestHeader String authorization) {
    jwtAgent.validateJwt(authorization);
    Long userId = jwtAgent.getId(authorization);

    timetableService.deleteTimetable(timetableId, userId);
    return ApiResponse.success(ResultResponse.complete());
  }

  @GetMapping
  public ApiResponse<List<SimpleTimetableResponse>> getMyAllTimetableList(@RequestHeader String authorization) {
    jwtAgent.validateJwt(authorization);
    Long userId = jwtAgent.getId(authorization);

    return ApiResponse.success(timetableService.getMyAllTimetableList(userId));
  }

  @GetMapping("/{timetableId}")
  public ApiResponse<TimetableResponse> getTimetable(@PathVariable Long timetableId, @RequestHeader String authorization) {
    jwtAgent.validateJwt(authorization);

    return ApiResponse.success(timetableService.getTimetable(timetableId));
  }

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

  @PutMapping("/cells/{cellId}")
  public ApiResponse<TimetableCellResponse> updateTimetableCell(
    @PathVariable Long cellId,
    @RequestHeader String authorization,
    @Valid @RequestBody UpdateTimetableCellRequest request
  ) {
    jwtAgent.validateJwt(authorization);
    Long userId = jwtAgent.getId(authorization);

    return ApiResponse.success(timetableService.updateTimetableCell(request, cellId, userId));
  }

  @DeleteMapping("/cells/{cellId}")
  public ApiResponse<ResultResponse> deleteTimetableCell(@PathVariable Long cellId, @RequestHeader String authorization) {
    jwtAgent.validateJwt(authorization);
    Long userId = jwtAgent.getId(authorization);

    timetableService.deleteTimetableCell(cellId, userId);
    return ApiResponse.success(ResultResponse.complete());
  }
}
