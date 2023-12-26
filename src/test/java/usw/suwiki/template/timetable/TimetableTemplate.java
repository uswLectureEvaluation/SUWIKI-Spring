package usw.suwiki.template.timetable;

import usw.suwiki.domain.timetable.entity.Semester;
import usw.suwiki.domain.timetable.entity.Timetable;
import usw.suwiki.domain.user.user.User;

public class TimetableTemplate {
    public static final long ID = 1L;
    public static final String NAME = "첫번째 시간표";
    public static final int YEAR = 2023;
    public static final Semester SEMESTER = Semester.FIRST;

    public static Timetable createFirstDummyTimetable(User user) { // 유저 외 연관관계 엔티티 없는 시간표 생성
        Timetable timetable = Timetable.builder()
                .name(NAME)
                .year(YEAR)
                .semester(SEMESTER)
                .build();
        timetable.associateUser(user);
        return timetable;
    }

    public static Timetable createCustomDummyTimetable(String name, Integer year, Semester semester, User user) {
        Timetable timetable = Timetable.builder()
                .name(name)
                .year(year)
                .semester(semester)
                .build();
        timetable.associateUser(user);
        return timetable;
    }
}
