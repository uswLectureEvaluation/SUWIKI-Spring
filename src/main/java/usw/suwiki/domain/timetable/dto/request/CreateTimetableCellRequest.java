package usw.suwiki.domain.timetable.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.timetable.entity.Timetable;
import usw.suwiki.domain.timetable.entity.TimetableCell;
import usw.suwiki.domain.timetable.entity.TimetableCellColor;
import usw.suwiki.domain.timetable.entity.TimetableCellSchedule;
import usw.suwiki.domain.timetable.entity.TimetableDay;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateTimetableCellRequest {
    public static final int MAX_LECTURE_NAME_LENGTH = 150;
    public static final int MAX_PROFESSOR_NAME_LENGTH = 130;
    public static final int MAX_COLOR_LENGTH = 50;
    public static final int MAX_LOCATION_LENGTH = 150;
    public static final int MAX_DAY_LENGTH = 50;

    @NotNull
    @Size(max = MAX_LECTURE_NAME_LENGTH)
    private String lectureName;

    @NotNull
    @Size(max = MAX_PROFESSOR_NAME_LENGTH)
    private String professorName;

    @NotBlank
    @Size(max = MAX_COLOR_LENGTH)
    private String color;

    @NotNull
    @Size(max = MAX_LOCATION_LENGTH)
    private String location;

    @NotBlank
    @Size(max = MAX_DAY_LENGTH)
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

    public TimetableCellSchedule extractTimetableCellSchedule() {
        return TimetableCellSchedule.builder()
                .location(location)
                .day(TimetableDay.ofString(day))
                .startPeriod(startPeriod)
                .endPeriod(endPeriod)
                .build();
    }
}
