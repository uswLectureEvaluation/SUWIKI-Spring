package usw.suwiki.domain.timetable.entity;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.TimetableException;
import usw.suwiki.global.util.enums.KeyValueEnumModel;

@RequiredArgsConstructor
public enum TimetableCellColor implements KeyValueEnumModel<String> {
    ORANGE("ORANGE"), APRICOT("APRICOT"), PINK("PINK"), SKY("SKY"),
    BROWN("BROWN"), LIGHT_BROWN("LIGHT_BROWN"), BROWN_DARK("BROWN_DARK"),
    PURPLE("PURPLE"), PURPLE_LIGHT("PURPLE_LIGHT"),
    RED_LIGHT("RED_LIGHT"),
    GREEN("GREEN"), GREEN_LIGHT("GREEN_LIGHT"), GREEN_DARK("GREEN_DARK"),
    NAVY("NAVY"), NAVY_LIGHT("NAVY_LIGHT"), NAVY_DARK("NAVY_DARK"),
    VIOLET("VIOLET"), VIOLET_LIGHT("VIOLET_LIGHT"),
    GRAY("GRAY"), GRAY_DARK("GRAY_DARK")
    ;

    private final String value;

    public static TimetableCellColor ofString(String param) {
        return Arrays.stream(TimetableCellColor.values())
                .filter(v -> v.getValue().equals(param.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new TimetableException(ExceptionType.INVALID_TIMETABLE_CELL_COLOR));
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
