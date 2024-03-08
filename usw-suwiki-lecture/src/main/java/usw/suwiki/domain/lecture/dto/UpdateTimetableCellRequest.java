package usw.suwiki.domain.lecture.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Builder
@AllArgsConstructor
public class UpdateTimetableCellRequest {
    @NotNull
    @Size(max = 150)
    private String lectureName;

    @NotNull
    @Size(max = 130)
    private String professorName;

    @NotBlank
    @Size(max = 50)
    private String color;

    @NotNull
    @Size(max = 150)
    private String location;

    @NotBlank
    @Size(max = 50)
    private String day;

    private Integer startPeriod;

    private Integer endPeriod;

    public TimetableCellSchedule extractTimetableCellSchedule() {
        return TimetableCellSchedule.builder()
            .location(location)
            .day(TimetableDay.ofString(day))
            .startPeriod(startPeriod)
            .endPeriod(endPeriod)
            .build();
    }
}
