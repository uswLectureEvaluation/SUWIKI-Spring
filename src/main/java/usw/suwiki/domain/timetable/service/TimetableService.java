package usw.suwiki.domain.timetable.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.timetable.dto.request.CreateTimetableRequest;
import usw.suwiki.domain.timetable.dto.response.CreateTimetableResponse;
import usw.suwiki.domain.timetable.entity.Timetable;
import usw.suwiki.domain.timetable.repository.TimetableRepository;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.service.UserCRUDService;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TimetableService {
    private final TimetableRepository timetableRepository;
    private final UserCRUDService userCRUDService;

    // 시간표 생성
    @Transactional
    public CreateTimetableResponse createTimetable(CreateTimetableRequest request, Long userId) {
        User user = userCRUDService.loadUserById(userId);

        Timetable timetable = timetableRepository.save(request.toEntity(user));
        return CreateTimetableResponse.from(timetable);
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
