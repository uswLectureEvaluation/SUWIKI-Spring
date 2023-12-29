package usw.suwiki.domain.timetable.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UpdateTimetableRequest {
    @NotNull
    private String name;
    @NotBlank
    private Integer year;
    @NotNull
    private String semester;

}
