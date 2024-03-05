package usw.suwiki.domain.timetable.entity;

import java.util.Objects;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.core.exception.errortype.TimetableException;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableCellSchedule {

    @NotNull
    @Size(max = 200)
    private String location;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TimetableDay day;

    @Min(value = 1)
    @Max(value = 24)
    private Integer startPeriod;

    @Min(value = 1)
    @Max(value = 24)
    private Integer endPeriod;

    @Builder
    public TimetableCellSchedule(String location, TimetableDay day, Integer startPeriod, Integer endPeriod) {
        this.location = location;
        this.day = day;

        validatePeriods(startPeriod, endPeriod);
        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
    }

    private void validatePeriods(Integer start, Integer end) {
        if (Objects.isNull(start) || Objects.isNull(end)) {
            return;
        }

        if (start > end) {
            throw new TimetableException(ExceptionType.INVALID_TIMETABLE_CELL_SCHEDULE);
        }
    }

    public boolean isOverlapped(TimetableCellSchedule otherSchedule) {
        return this.day.equals(otherSchedule.day) &&
            Math.max(this.startPeriod, otherSchedule.startPeriod)
                <= Math.min(this.endPeriod, otherSchedule.getEndPeriod());
    }
}
