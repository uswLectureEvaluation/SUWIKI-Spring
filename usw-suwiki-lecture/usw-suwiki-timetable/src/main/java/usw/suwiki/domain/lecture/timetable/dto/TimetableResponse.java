package usw.suwiki.domain.lecture.timetable.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimetableResponse {

  @Data
  public static class Detail {
    private final Long id;
    private final String name;
    private final Integer year;
    private final String semester;
    private final List<Cell> cells;
  }

  @Data
  public static class Simple {
    private final Long id;
    private final String name;
    private final Integer year;
    private final String semester;
  }

  @Data
  public static class Cell {
    private final int cellIdx;
    private final String lecture;
    private final String professor;
    private final String color;
    private final String location;
    private final String day;
    private final Integer startPeriod;
    private final Integer endPeriod;
  }
}
