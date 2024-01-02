package usw.suwiki.domain.timetable.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.timetable.dto.request.CreateTimetableCellRequest;
import usw.suwiki.domain.timetable.dto.request.CreateTimetableRequest;
import usw.suwiki.domain.timetable.dto.request.UpdateTimetableCellRequest;
import usw.suwiki.domain.timetable.dto.request.UpdateTimetableRequest;
import usw.suwiki.domain.timetable.dto.response.SimpleTimetableResponse;
import usw.suwiki.domain.timetable.dto.response.TimetableCellResponse;
import usw.suwiki.domain.timetable.dto.response.TimetableResponse;
import usw.suwiki.domain.timetable.entity.Semester;
import usw.suwiki.domain.timetable.entity.Timetable;
import usw.suwiki.domain.timetable.entity.TimetableCell;
import usw.suwiki.domain.timetable.entity.TimetableCellColor;
import usw.suwiki.domain.timetable.entity.TimetableCellSchedule;
import usw.suwiki.domain.timetable.repository.TimetableCellRepository;
import usw.suwiki.domain.timetable.repository.TimetableRepository;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.service.UserCRUDService;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.TimetableException;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TimetableService {
    private final TimetableRepository timetableRepository;
    private final TimetableCellRepository timetableCellRepository;
    private final UserCRUDService userCRUDService;

    @Transactional
    public SimpleTimetableResponse createTimetable(CreateTimetableRequest request, Long userId) {
        User user = userCRUDService.loadUserById(userId);

        Timetable timetable = timetableRepository.save(request.toEntity(user));
        return SimpleTimetableResponse.of(timetable);
    }

    @Transactional
    public SimpleTimetableResponse updateTimetable(UpdateTimetableRequest request, Long timetableId, Long userId) {
        Timetable timetable = resolveExactAuthorTimetable(timetableId, userId);

        timetable.update(request.getName(), request.getYear(), Semester.ofString(request.getSemester()));
        return SimpleTimetableResponse.of(timetable);
    }

    @Transactional
    public void deleteTimetable(Long timetableId, Long userId) {
        User user = userCRUDService.loadUserById(userId);
        Timetable timetable = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new TimetableException(ExceptionType.TIMETABLE_NOT_FOUND));
        timetable.validateIsAuthor(user);

        timetable.dissociateUser(user);
    }

    public List<SimpleTimetableResponse> getMyAllTimetableList(Long userId) {
        List<Timetable> timetableList = timetableRepository.findAllByUserId(userId);

        return timetableList.stream()
                .map(SimpleTimetableResponse::of)
                .toList();
    }

    public TimetableResponse getTimetable(Long timetableId) {
        Timetable timetable = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new TimetableException(ExceptionType.TIMETABLE_NOT_FOUND));

        return TimetableResponse.of(timetable);
    }

    @Transactional
    public TimetableCellResponse createTimetableCell(CreateTimetableCellRequest request, Long timetableId,
                                                     Long userId) {
        Timetable timetable = resolveExactAuthorTimetable(timetableId, userId);
        timetable.validateCellScheduleOverlap(request.extractTimetableCellSchedule());

        TimetableCell timetableCell = timetableCellRepository.save(request.toEntity(timetable));
        return TimetableCellResponse.of(timetableCell);
    }

    @Transactional
    public TimetableCellResponse updateTimetableCell(UpdateTimetableCellRequest request, Long cellId, Long userId) {
        TimetableCell timetableCell = timetableCellRepository.findById(cellId)
                .orElseThrow(() -> new TimetableException(ExceptionType.TIMETABLE_CELL_NOT_FOUND));
        Timetable timetable = resolveExactAuthorTimetable(timetableCell.bringTimetableId(), userId);
        TimetableCellSchedule cellSchedule = request.extractTimetableCellSchedule();
        timetable.validateCellScheduleOverlapExceptOneCell(cellSchedule, timetableCell);

        timetableCell.update(
                request.getLectureName(),
                request.getProfessorName(),
                TimetableCellColor.ofString(request.getColor()),
                cellSchedule
        );
        return TimetableCellResponse.of(timetableCell);
    }

    @Transactional
    public void deleteTimetableCell(Long cellId, Long userId) {
        TimetableCell timetableCell = timetableCellRepository.findById(cellId)
                .orElseThrow(() -> new TimetableException(ExceptionType.TIMETABLE_CELL_NOT_FOUND));
        Timetable timetable = resolveExactAuthorTimetable(timetableCell.bringTimetableId(), userId);

        timetableCell.dissociateTimetable(timetable);
    }

    // 시간표 일괄 DB 동기화 (시간표 및 강의 bulk 생성)


    private Timetable resolveExactAuthorTimetable(Long timetableId, Long userId) {
        User user = userCRUDService.loadUserById(userId);
        Timetable timetable = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new TimetableException(ExceptionType.TIMETABLE_NOT_FOUND));
        timetable.validateIsAuthor(user);
        return timetable;
    }

}
