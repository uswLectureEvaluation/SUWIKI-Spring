package usw.suwiki.domain.timetable.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.timetable.entity.Semester;
import usw.suwiki.domain.timetable.entity.Timetable;
import usw.suwiki.domain.user.user.User;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CreateTimetableRequest {
    @NotBlank(message = "NOT NULL")
    private Integer year;
    @NotNull
    private String semester;
    @NotNull
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
