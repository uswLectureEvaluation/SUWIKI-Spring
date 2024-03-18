package usw.suwiki.domain.lecture.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimetableRequest {

  @Data
  public static class Create {
    @Valid
    private final Tag tag;
    @Valid
    private final List<Cell> cells;
  }

  @Data
  public static class Tag {
    @Min(2020)
    private final int year;
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
    @Positive
    private int startPeriod;
    @Positive
    private int endPeriod;
  }
}
