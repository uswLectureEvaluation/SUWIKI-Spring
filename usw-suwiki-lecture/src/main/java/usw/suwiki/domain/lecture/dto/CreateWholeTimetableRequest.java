package usw.suwiki.domain.lecture.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CreateWholeTimetableRequest {
    @NotNull
    private Integer year;

    @NotNull
    @Size(max = 50)
    private String semester;

    @NotNull
    @Size(max = 200)
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
