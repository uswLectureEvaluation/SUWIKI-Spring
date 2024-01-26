package usw.suwiki.domain.timetable.fixture;

import usw.suwiki.domain.timetable.entity.Semester;
import usw.suwiki.domain.timetable.entity.Timetable;
import usw.suwiki.domain.user.user.User;

public class TimetableFixture {
    public static final String NAME = "첫번째 시간표";
    public static final int YEAR = 2023;
    public static final Semester SEMESTER = Semester.FIRST;

    public static Timetable createFirstDummy(User user) { // 유저 외 연관관계 엔티티 없는 시간표 생성
        Timetable timetable = Timetable.builder()
                .name(NAME)
                .year(YEAR)
                .semester(SEMESTER)
                .build();
        timetable.associateUser(user);
        return timetable;
    }

    public static Timetable createDummy(String name, Integer year, Semester semester, User user) {
        Timetable timetable = Timetable.builder()
                .name(name)
                .year(year)
                .semester(semester)
                .build();
        timetable.associateUser(user);
        return timetable;
    }
}
