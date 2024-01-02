package usw.suwiki.domain.timetable.entity;

import java.util.stream.IntStream;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableCellSchedule {
    @NotNull
    @Size(max = 200)
    private String location;    // blank 가능

    @Enumerated(EnumType.STRING)
    @NotNull
    private TimetableDay day;

    @Min(value = 1)
    @Max(value = 24)
    private Integer startPeriod;

    @Min(value = 1)
    @Max(value = 24)
    private Integer endPeriod;

    // 셀끼리 요일이 같은 상태에서 교시가 하나라도 겹치는지 여부
    public boolean isOverlapped(TimetableCellSchedule otherSchedule) {
        return this.day.equals(otherSchedule.day) &&
                Math.max(this.startPeriod, otherSchedule.startPeriod)
                        <= Math.min(this.endPeriod, otherSchedule.getEndPeriod());
    }
}
