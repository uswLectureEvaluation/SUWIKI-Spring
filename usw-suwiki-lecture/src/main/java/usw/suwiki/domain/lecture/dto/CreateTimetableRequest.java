package usw.suwiki.domain.lecture.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import usw.suwiki.domain.lecture.timetable.Semester;
import usw.suwiki.domain.lecture.timetable.Timetable;
import usw.suwiki.domain.user.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Getter
@Builder
@AllArgsConstructor
public class CreateTimetableRequest {
    @NotNull
    private Integer year;

    @NotNull
    @Size(max = 50)
    private String semester;

    @NotNull
    @Size(max = 150)
    private String name;

    public Timetable toEntity(User user) {
        Semester semester = Semester.of(this.semester);
        Timetable timetable = Timetable.builder()
            .year(year)
            .semester(semester)
            .name(name)
            .build();
        timetable.associateUser(user);
        return timetable;
    }
}
