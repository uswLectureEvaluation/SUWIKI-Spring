package usw.suwiki.template.timetableelement;

import usw.suwiki.domain.timetable.entity.TimetableCell;
import usw.suwiki.domain.timetable.entity.TimetableDay;
import usw.suwiki.domain.timetable.entity.TimetableElement;

public class TimetableElementTemplate {
    public static final String LOCATION = "더미 102";
    public static final TimetableDay DAY = TimetableDay.MON;
    public static final int PERIOD = 1;

    public static TimetableElement createFirstDummy(TimetableCell cell) {
        return createDummy(LOCATION, DAY, PERIOD, cell);
    }

    public static TimetableElement createDummy(String location, TimetableDay day, Integer period, TimetableCell cell) {
        TimetableElement timetableElement = TimetableElement.builder()
                .location(location)
                .day(day)
                .period(period)
                .build();
        timetableElement.associateTimetableCell(cell);
        return timetableElement;
    }
}
