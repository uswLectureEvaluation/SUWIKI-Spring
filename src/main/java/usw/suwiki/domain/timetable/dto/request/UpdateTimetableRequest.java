package usw.suwiki.domain.timetable.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UpdateTimetableRequest {

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

}
