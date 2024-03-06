package usw.suwiki.domain.lecture.timetable;

import usw.suwiki.common.data.KeyValueEnumModel;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.TimetableException;

public enum Semester implements KeyValueEnumModel<String> {
    FIRST,
    SECOND,
    SUMMER,
    WINTER,
    ;

    public static Semester of(String param) {
        try {
            return Enum.valueOf(Semester.class, param.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new TimetableException(ExceptionType.INVALID_TIMETABLE_SEMESTER);
        }
    }

    @Override
    public String getKey() {
        return this.name();
    }

    @Override
    public String getValue() {
        return this.name();
    }
}
