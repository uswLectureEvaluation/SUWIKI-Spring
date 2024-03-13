package usw.suwiki.domain.lecture.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import usw.suwiki.domain.lecture.timetable.TimetableCell;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TimetableCellResponse {

    long id;
    String lectureName;
    String professorName;
    String color;
    String location;
    String day;
    int startPeriod;
    int endPeriod;

    public static TimetableCellResponse of(TimetableCell cell) {
        return builder()
            .id(cell.getId())
            .lectureName(cell.getLectureName())
            .professorName(cell.getProfessorName())
            .color(cell.getColor().getValue())
            .location(cell.getSchedule().getLocation())
            .day(cell.getSchedule().getDay().getValue())
            .startPeriod(cell.getSchedule().getStartPeriod())
            .endPeriod(cell.getSchedule().getEndPeriod())
            .build();
    }
}
