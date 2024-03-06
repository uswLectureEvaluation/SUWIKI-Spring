package usw.suwiki.domain.lecture.timetable;

import lombok.RequiredArgsConstructor;
import usw.suwiki.core.exception.errortype.TimetableException;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.util.enums.KeyValueEnumModel;

import java.util.Arrays;

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

    public static TimetableDay ofKorean(String param) {
        return switch (param) {
            case "월" -> MON;
            case "화" -> TUE;
            case "수" -> WED;
            case "목" -> THU;
            case "금" -> FRI;
            case "토" -> SAT;
            case "일" -> SUN;

            default -> throw new TimetableException(ExceptionType.INVALID_TIMETABLE_CELL_DAY);
        };
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
