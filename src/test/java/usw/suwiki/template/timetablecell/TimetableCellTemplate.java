package usw.suwiki.template.timetablecell;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import usw.suwiki.domain.timetable.entity.Timetable;
import usw.suwiki.domain.timetable.entity.TimetableCell;
import usw.suwiki.domain.timetable.entity.TimetableCellColor;
import usw.suwiki.domain.timetable.entity.TimetableDay;

public class TimetableCellTemplate {
    public static final String LECTURE_NAME = "더미 강의";
    public static final String PROFESSOR_NAME = "더미 교수";
    public static final TimetableCellColor COLOR = TimetableCellColor.GRAY;
    public static final String LOCATION = "RANDOM 101";    // blank 가능
    private static final TimetableDay DAY = TimetableDay.MON;
    private static final int START_PERIOD = 1;
    private static final int END_PERIOD = 3;

    public static TimetableCell createFirstDummy(Timetable timetable) {
        return createDummy(timetable, LECTURE_NAME, PROFESSOR_NAME, COLOR, LOCATION, DAY, START_PERIOD, END_PERIOD);
    }

    public static TimetableCell createDummy(
            Timetable timetable,
            String lectureName,
            String professorName,
            TimetableCellColor color,
            String location,
            TimetableDay day,
            Integer startPeriod,
            Integer endPeriod
    ) {
        TimetableCell timetableCell = TimetableCell.builder()
                .lectureName(lectureName)
                .professorName(professorName)
                .color(color)
                .location(location)
                .day(day)
                .startPeriod(startPeriod)
                .endPeriod(endPeriod)
                .build();
        timetableCell.associateTimetable(timetable);
        return timetableCell;
    }
}
