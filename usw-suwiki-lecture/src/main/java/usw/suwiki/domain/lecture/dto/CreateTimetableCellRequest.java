package usw.suwiki.domain.lecture.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import usw.suwiki.domain.lecture.timetable.Timetable;
import usw.suwiki.domain.lecture.timetable.TimetableCell;
import usw.suwiki.domain.lecture.timetable.TimetableCellColor;
import usw.suwiki.domain.lecture.timetable.TimetableCellSchedule;
import usw.suwiki.domain.lecture.timetable.TimetableDay;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Builder
@AllArgsConstructor
public class CreateTimetableCellRequest {
    @NotNull
    @Size(max = 150)
    private String lectureName;

    @NotNull
    @Size(max = 130)
    private String professorName;

    @NotBlank
    @Size(max = 50)
    private String color;

    @NotNull
    @Size(max = 150)
    private String location;

    @NotBlank
    @Size(max = 50)
    private String day;

    private Integer startPeriod;

    private Integer endPeriod;

    public TimetableCell toEntity(Timetable timetable) {
        TimetableCell timetableCell = TimetableCell.builder()
            .lectureName(lectureName)
            .professorName(professorName)
            .color(TimetableCellColor.ofString(color))
            .schedule(extractTimetableCellSchedule())
            .build();
        timetableCell.associateTimetable(timetable);
        return timetableCell;
    }

    public TimetableCell toEntity() {
        return TimetableCell.builder()
            .lectureName(lectureName)
            .professorName(professorName)
            .color(TimetableCellColor.ofString(color))
            .schedule(extractTimetableCellSchedule())
            .build();
    }


    public TimetableCellSchedule extractTimetableCellSchedule() {
        return TimetableCellSchedule.builder()
            .location(location)
            .day(TimetableDay.ofString(day))
            .startPeriod(startPeriod)
            .endPeriod(endPeriod)
            .build();
    }
}
