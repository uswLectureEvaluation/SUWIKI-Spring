package usw.suwiki.domain.timetable.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.timetable.entity.Semester;
import usw.suwiki.domain.timetable.entity.Timetable;
import usw.suwiki.domain.user.user.User;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateTimetableRequest {
    public static final int MAX_NAME_LENGTH = 150;
    public static final int MAX_SEMESTER_LENGTH = 50;

    @NotNull
    private Integer year;

    @NotNull
    @Size(max = MAX_SEMESTER_LENGTH)
    private String semester;

    @NotNull
    @Size(max = MAX_NAME_LENGTH)
    private String name;

    public Timetable toEntity(User user) {
        Semester semester = Semester.ofString(this.semester);
        Timetable timetable = Timetable.builder()
                .year(year)
                .semester(semester)
                .name(name)
                .build();
        timetable.associateUser(user);
        return timetable;
    }
}
