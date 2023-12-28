package usw.suwiki.domain.timetable.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.timetable.entity.TimetableCellColor;
import usw.suwiki.domain.timetable.repository.TimetableRepository;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TimetableService {
    private TimetableRepository timetableRepository;

    // 시간표 생성
    public void createTimetable() {
        String param = "PINK";

        TimetableCellColor color = TimetableCellColor.ofString(param);
        System.out.println("color = " + color);

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
