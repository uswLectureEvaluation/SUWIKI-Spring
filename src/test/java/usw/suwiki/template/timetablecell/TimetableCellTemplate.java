package usw.suwiki.template.timetablecell;

import usw.suwiki.domain.timetable.entity.Timetable;
import usw.suwiki.domain.timetable.entity.TimetableCell;
import usw.suwiki.domain.timetable.entity.TimetableCellColor;

public class TimetableCellTemplate {
    public static final String LECTURE_NAME = "더미 강의";
    public static final String PROFESSOR_NAME = "더미 교수";
    public static final TimetableCellColor COLOR = TimetableCellColor.GRAY;

    public static TimetableCell createFirstDummy(Timetable timetable) {
        return createDummy(LECTURE_NAME, PROFESSOR_NAME, COLOR, timetable);
    }

    public static TimetableCell createDummy(
            String lectureName,
            String professorName,
            TimetableCellColor color,
            Timetable timetable
    ) {
        TimetableCell timetableCell = TimetableCell.builder()
                .lectureName(lectureName)
                .professorName(professorName)
                .color(color)
                .build();
        timetableCell.associateTimetable(timetable);
        return timetableCell;
    }
}
