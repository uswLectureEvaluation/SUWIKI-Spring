package usw.suwiki.domain.timetable.entity;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.TimetableException;
import usw.suwiki.global.util.enums.KeyValueEnumModel;

@RequiredArgsConstructor
public enum TimetableDay implements KeyValueEnumModel<String> {
    MON("MON"),
    TUE("TUE"),
    WED("WED"),
    THU("THU"),
    FRI("FRI"),
    SAT("SAT"),
    SUN("SUN"),
    E_LEARNING("E_LEARNING");

    private final String value;

    public static TimetableDay ofString(String param) {
        return Arrays.stream(TimetableDay.values())
                .filter(v -> v.getValue().equals(param.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new TimetableException(ExceptionType.INVALID_TIMETABLE_CELL_DAY));
    }

    @Override
    public String getKey() {
        return this.name();
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
