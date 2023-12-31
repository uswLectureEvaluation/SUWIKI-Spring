package usw.suwiki.domain.timetable.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import usw.suwiki.domain.timetable.entity.TimetableCell;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
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
        return TimetableCellResponse.builder()
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
