package usw.suwiki.domain.timetable.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.timetable.dto.request.CreateTimetableRequest;
import usw.suwiki.domain.timetable.dto.request.UpdateTimetableRequest;
import usw.suwiki.domain.timetable.dto.response.TimetableResponse;
import usw.suwiki.domain.timetable.entity.Semester;
import usw.suwiki.domain.timetable.entity.Timetable;
import usw.suwiki.domain.timetable.repository.TimetableRepository;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.service.UserCRUDService;
import usw.suwiki.global.dto.ResultResponse;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.TimetableException;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TimetableService {
    private final TimetableRepository timetableRepository;
    private final UserCRUDService userCRUDService;

    // 시간표 생성
    @Transactional
    public TimetableResponse createTimetable(CreateTimetableRequest request, Long userId) {
        User user = userCRUDService.loadUserById(userId);

        Timetable timetable = timetableRepository.save(request.toEntity(user));
        return TimetableResponse.of(timetable);
    }

    // 시간표 수정
    @Transactional
    public TimetableResponse updateTimetable(UpdateTimetableRequest request, Long timetableId, Long userId) {
        User user = userCRUDService.loadUserById(userId);
        Timetable timetable = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new TimetableException(ExceptionType.TIMETABLE_NOT_FOUND));
        timetable.validateIsAuthor(user);

        timetable.update(request.getName(), request.getYear(), Semester.ofString(request.getSemester()));
        return TimetableResponse.of(timetable);
    }

    // 시간표 삭제
    @Transactional
    public ResultResponse deleteTimetable(Long timetableId, Long userId) {
        User user = userCRUDService.loadUserById(userId);
        Timetable timetable = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new TimetableException(ExceptionType.TIMETABLE_NOT_FOUND));
        timetable.validateIsAuthor(user);

        timetableRepository.delete(timetable);
        return ResultResponse.of(true);
    }

    // 시간표 리스트 조회

    // 시간표 상세 조회

    // 시간표 강의 - 생성

    // 시간표 강의 - 수정

    // 시간표 강의 - 삭제

    // 시간표 일괄 DB 동기화 (시간표 및 강의 bulk 생성)
}
