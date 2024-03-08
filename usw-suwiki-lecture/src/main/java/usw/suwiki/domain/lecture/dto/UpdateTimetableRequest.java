package usw.suwiki.domain.lecture.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Builder
@AllArgsConstructor
public class UpdateTimetableRequest {
    @NotNull
    private Integer year;

    @NotNull
    @Size(max = 50)
    private String semester;

    @NotNull
    @Size(max = 150)
    private String name;
}
