package usw.suwiki.domain.lecture.timetable;

import lombok.RequiredArgsConstructor;
import usw.suwiki.core.exception.errortype.TimetableException;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.util.enums.KeyValueEnumModel;

import java.util.Arrays;

@RequiredArgsConstructor
public enum Semester implements KeyValueEnumModel<String> {
    FIRST("FIRST"),
    SECOND("SECOND"),
    SUMMER("SUMMER"),
    WINTER("WINTER"),
    ;

    private final String value;

    public static Semester of(String param) {
        return Arrays.stream(Semester.values())
            .filter(v -> v.getValue().equals(param.toUpperCase()))
            .findFirst()
            .orElseThrow(() -> new TimetableException(ExceptionType.INVALID_TIMETABLE_SEMESTER));
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
