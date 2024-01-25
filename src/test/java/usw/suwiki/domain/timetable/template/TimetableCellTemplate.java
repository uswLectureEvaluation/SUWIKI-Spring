package usw.suwiki.domain.timetable.template;

import usw.suwiki.domain.timetable.entity.Timetable;
import usw.suwiki.domain.timetable.entity.TimetableCell;
import usw.suwiki.domain.timetable.entity.TimetableCellColor;
import usw.suwiki.domain.timetable.entity.TimetableCellSchedule;
import usw.suwiki.domain.timetable.entity.TimetableDay;

public class TimetableCellTemplate {
    public static final String LECTURE_NAME_A = "더미 강의 A";
    public static final String LECTURE_NAME_B = "더미 강의 B";
    public static final String PROFESSOR_NAME_A = "더미 교수 A";
    public static final String PROFESSOR_NAME_B = "더미 교수 B";
    public static final TimetableCellColor COLOR_A = TimetableCellColor.RED_LIGHT;
    public static final TimetableCellColor COLOR_B = TimetableCellColor.GRAY;
    public static final String LOCATION = "RANDOM 101";    // blank 가능
    private static final TimetableDay DAY_A = TimetableDay.MON;
    private static final TimetableDay DAY_B = TimetableDay.WED;
    private static final int START_PERIOD_A = 1;
    private static final int START_PERIOD_B = 4;
    private static final int END_PERIOD_A = 3;
    private static final int END_PERIOD_B = 6;

    public static TimetableCell createFirstDummy(Timetable timetable) {
        return createDummy(
                timetable, LECTURE_NAME_A, PROFESSOR_NAME_A, COLOR_A, LOCATION, DAY_A, START_PERIOD_A, END_PERIOD_A
        );
    }

    public static TimetableCell createSecondDummy(Timetable timetable) {
        return createDummy(
                timetable, LECTURE_NAME_B, PROFESSOR_NAME_B, COLOR_B, LOCATION, DAY_B, START_PERIOD_B, END_PERIOD_B
        );
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
        TimetableCellSchedule schedule = TimetableCellSchedule.builder()
                .location(location)
                .day(day)
                .startPeriod(startPeriod)
                .endPeriod(endPeriod)
                .build();
        TimetableCell timetableCell = TimetableCell.builder()
                .lectureName(lectureName)
                .professorName(professorName)
                .color(color)
                .schedule(schedule)
                .build();
        timetableCell.associateTimetable(timetable);
        return timetableCell;
    }

    public static TimetableCell createOrphanDummy(
            String lectureName,
            String professorName,
            TimetableCellColor color,
            String location,
            TimetableDay day,
            Integer startPeriod,
            Integer endPeriod
    ) {
        TimetableCellSchedule schedule = TimetableCellSchedule.builder()
                .location(location)
                .day(day)
                .startPeriod(startPeriod)
                .endPeriod(endPeriod)
                .build();
        return TimetableCell.builder()
                .lectureName(lectureName)
                .professorName(professorName)
                .color(color)
                .schedule(schedule)
                .build();
    }
}
