package usw.suwiki.domain.lecture.timetable;

import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.TimetableException;

enum Semester {
  FIRST,
  SECOND,
  SUMMER,
  WINTER,
  ;

  public static Semester from(String param) {
    try {
      return Enum.valueOf(Semester.class, param.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new TimetableException(ExceptionType.INVALID_TIMETABLE_SEMESTER);
    }
  }
}
