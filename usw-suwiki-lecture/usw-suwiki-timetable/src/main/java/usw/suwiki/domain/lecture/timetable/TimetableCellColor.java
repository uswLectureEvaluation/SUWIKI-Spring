package usw.suwiki.domain.lecture.timetable;

import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.TimetableException;

public enum TimetableCellColor {
  ORANGE,
  APRICOT,
  PINK,
  SKY,
  BROWN,
  LIGHT_BROWN,
  BROWN_DARK,
  PURPLE,
  PURPLE_LIGHT,
  RED_LIGHT,
  GREEN,
  GREEN_LIGHT,
  GREEN_DARK,
  NAVY,
  NAVY_LIGHT,
  NAVY_DARK,
  VIOLET,
  VIOLET_LIGHT,
  GRAY,
  GRAY_DARK,
  ;

  public static TimetableCellColor from(String param) {
    try {
      return Enum.valueOf(TimetableCellColor.class, param.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new TimetableException(ExceptionType.INVALID_TIMETABLE_CELL_COLOR);
    }
  }
}
