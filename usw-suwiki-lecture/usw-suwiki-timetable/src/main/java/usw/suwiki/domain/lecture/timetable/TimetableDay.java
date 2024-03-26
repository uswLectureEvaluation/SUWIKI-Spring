package usw.suwiki.domain.lecture.timetable;

import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.TimetableException;

public enum TimetableDay {
  MON,
  TUE,
  WED,
  THU,
  FRI,
  SAT,
  SUN,
  E_LEARNING;

  public static TimetableDay from(String param) {
    try {
      return Enum.valueOf(TimetableDay.class, param.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new TimetableException(ExceptionType.INVALID_TIMETABLE_CELL_DAY);
    }
  }

  public static TimetableDay ofKorean(String param) {
    return switch (param) { // todo: e-러닝이 들어오면 예외? 파라미터랑 메서드 이름 수정
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

  public boolean isEquals(TimetableDay other) {
    return this == other;
  }
}
