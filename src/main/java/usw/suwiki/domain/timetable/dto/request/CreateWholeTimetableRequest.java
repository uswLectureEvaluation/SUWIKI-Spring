package usw.suwiki.domain.timetable.dto.request;

import java.util.List;
import javax.validation.Valid;
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
public class CreateWholeTimetableRequest {
    public static final int MAX_NAME_LENGTH = 200;
    public static final int MAX_SEMESTER_LENGTH = 50;

    @NotNull
    private Integer year;

    @NotNull
    @Size(max = MAX_SEMESTER_LENGTH)
    private String semester;

    @NotNull
    @Size(max = MAX_NAME_LENGTH)
    private String name;

    @Valid
    @NotNull
    private List<CreateTimetableCellRequest> cellList;

    public Timetable toEntity(User user) {
        Timetable timetable = Timetable.builder()
                .year(year)
                .semester(Semester.of(semester))
                .name(name)
                .build();
        timetable.associateUser(user);

        cellList.stream()
                .map(cellRequest -> cellRequest.toEntity())
                .forEach(cell -> {
                    timetable.validateCellScheduleOverlapBeforeAssociation(cell.getSchedule());
                    cell.associateTimetable(timetable);
                });
        return timetable;
    }
}
