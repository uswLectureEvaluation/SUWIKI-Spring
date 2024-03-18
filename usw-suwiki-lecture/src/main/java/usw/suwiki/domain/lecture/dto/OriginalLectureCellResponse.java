package usw.suwiki.domain.lecture.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import usw.suwiki.domain.lecture.timetable.TimetableCellSchedule;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OriginalLectureCellResponse {

  private String location;
  private String day;
  private Integer startPeriod;
  private Integer endPeriod;

  public static OriginalLectureCellResponse of(TimetableCellSchedule schedule) {
    return builder()
      .location(schedule.getLocation())
      .day(schedule.getDay().getValue())
      .startPeriod(schedule.getStartPeriod())
      .endPeriod(schedule.getEndPeriod())
      .build();
  }
}
