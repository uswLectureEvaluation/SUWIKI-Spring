package usw.suwiki.domain.lecture.timetable.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.TimetableException;
import usw.suwiki.domain.lecture.dto.CreateTimetableCellRequest;
import usw.suwiki.domain.lecture.dto.CreateTimetableRequest;
import usw.suwiki.domain.lecture.dto.CreateWholeTimetableRequest;
import usw.suwiki.domain.lecture.dto.SimpleTimetableResponse;
import usw.suwiki.domain.lecture.dto.TimetableCellResponse;
import usw.suwiki.domain.lecture.dto.TimetableResponse;
import usw.suwiki.domain.lecture.dto.UpdateTimetableCellRequest;
import usw.suwiki.domain.lecture.dto.UpdateTimetableRequest;
import usw.suwiki.domain.lecture.timetable.Semester;
import usw.suwiki.domain.lecture.timetable.Timetable;
import usw.suwiki.domain.lecture.timetable.TimetableCell;
import usw.suwiki.domain.lecture.timetable.TimetableCellColor;
import usw.suwiki.domain.lecture.timetable.TimetableCellRepository;
import usw.suwiki.domain.lecture.timetable.TimetableCellSchedule;
import usw.suwiki.domain.lecture.timetable.TimetableRepository;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.service.UserCRUDService;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TimetableService {
    private final UserCRUDService userCRUDService;
    private final TimetableRepository timetableRepository;
    private final TimetableCellRepository timetableCellRepository;

    @Transactional
    public SimpleTimetableResponse createTimetable(CreateTimetableRequest request, Long userId) {
        User user = userCRUDService.loadUserById(userId);

        Timetable timetable = timetableRepository.save(request.toEntity(user));
        return SimpleTimetableResponse.of(timetable);
    }

    @Transactional
    public List<TimetableResponse> bulkCreateTimetables(List<CreateWholeTimetableRequest> requests, Long userId) {
        User user = userCRUDService.loadUserById(userId);

        List<Timetable> timetableList = timetableRepository.saveAll(
            requests.stream()
                .map(it -> it.toEntity(user))
                .toList()
        );

        return timetableList.stream()
            .map(TimetableResponse::of)
            .toList();
    }

    @Transactional
    public SimpleTimetableResponse updateTimetable(UpdateTimetableRequest request, Long timetableId, Long userId) {
        Timetable timetable = resolveExactAuthorTimetable(timetableId, userId);

        timetable.update(request.getName(), request.getYear(), Semester.of(request.getSemester()));
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
    public TimetableCellResponse createTimetableCell(CreateTimetableCellRequest request, Long timetableId, Long userId) {
        Timetable timetable = resolveExactAuthorTimetable(timetableId, userId);
        timetable.validateCellScheduleOverlapBeforeAssociation(request.extractTimetableCellSchedule());

        TimetableCell timetableCell = timetableCellRepository.save(request.toEntity(timetable));
        return TimetableCellResponse.of(timetableCell);
    }

    @Transactional
    public TimetableCellResponse updateTimetableCell(UpdateTimetableCellRequest request, Long cellId, Long userId) {
        TimetableCell timetableCell = timetableCellRepository.findById(cellId)
            .orElseThrow(() -> new TimetableException(ExceptionType.TIMETABLE_CELL_NOT_FOUND));

        Timetable timetable = resolveExactAuthorTimetable(timetableCell.bringTimetableId(), userId);
        TimetableCellSchedule cellSchedule = request.extractTimetableCellSchedule();
        timetable.validateCellScheduleOverlapAfterAssociation(cellSchedule, timetableCell);

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


    private Timetable resolveExactAuthorTimetable(Long timetableId, Long userId) {
        User user = userCRUDService.loadUserById(userId);
        Timetable timetable = timetableRepository.findById(timetableId)
            .orElseThrow(() -> new TimetableException(ExceptionType.TIMETABLE_NOT_FOUND));
        timetable.validateIsAuthor(user);
        return timetable;
    }
}
