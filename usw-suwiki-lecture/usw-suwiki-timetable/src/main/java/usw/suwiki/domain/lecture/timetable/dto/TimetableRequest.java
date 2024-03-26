package usw.suwiki.domain.lecture.timetable.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimetableRequest {

  @Data
  public static class Bulk {
    @Valid
    private final Description description;
    @Valid
    private final List<Cell> cells;
  }

  @Data
  public static class Description {
    @NotNull
    @Min(2020)
    private final Integer year;
    @NotNull
    @Size(max = 50)
    private final String semester;
    @NotNull
    @Size(max = 150)
    private final String name;
  }

  @Data
  public static class Cell {
    @NotNull
    @Size(max = 150)
    private final String lecture;
    @NotNull
    @Size(max = 130)
    private final String professor;
    @NotNull
    @Size(max = 50)
    private final String color;
    @NotNull
    @Size(max = 150)
    private final String location;
    @NotNull
    @Size(max = 50)
    private final String day;
    @Min(value = 1)
    @Max(value = 24)
    private Integer startPeriod;
    @Min(value = 1)
    @Max(value = 24)
    private Integer endPeriod;
  }

  @Data
  public static class UpdateCell {
    @PositiveOrZero
    private final int cellIdx;
    @NotNull
    @Size(max = 150)
    private final String lecture;
    @NotNull
    @Size(max = 130)
    private final String professor;
    @NotNull
    @Size(max = 50)
    private final String color;
    @NotNull
    @Size(max = 150)
    private final String location;
    @NotNull
    @Size(max = 50)
    private final String day;
    @Min(value = 1)
    @Max(value = 24)
    private Integer startPeriod;
    @Min(value = 1)
    @Max(value = 24)
    private Integer endPeriod;
  }
}
