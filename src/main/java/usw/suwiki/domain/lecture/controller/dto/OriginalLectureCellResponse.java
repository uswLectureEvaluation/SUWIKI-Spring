package usw.suwiki.domain.lecture.controller.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.timetable.entity.TimetableCellSchedule;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OriginalLectureCellResponse {

    private String location;
    private String day;
    private Integer startPeriod;
    private Integer endPeriod;

    public static OriginalLectureCellResponse of(TimetableCellSchedule schedule) {
        return OriginalLectureCellResponse.builder()
            .location(schedule.getLocation())
            .day(schedule.getDay().getValue())
            .startPeriod(schedule.getStartPeriod())
            .endPeriod(schedule.getEndPeriod())
            .build();
    }
}
