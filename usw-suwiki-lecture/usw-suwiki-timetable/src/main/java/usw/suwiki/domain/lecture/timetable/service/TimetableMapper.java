package usw.suwiki.domain.lecture.timetable.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.lecture.timetable.Timetable;
import usw.suwiki.domain.lecture.timetable.TimetableCell;
import usw.suwiki.domain.lecture.timetable.dto.TimetableRequest;
import usw.suwiki.domain.lecture.timetable.dto.TimetableResponse;

import java.util.List;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class TimetableMapper {

  public static TimetableCell toCell(TimetableRequest.Cell request) {
    return new TimetableCell(
      request.getLecture(),
      request.getProfessor(),
      request.getLocation(),
      request.getStartPeriod(),
      request.getEndPeriod(),
      request.getDay(),
      request.getColor()
    );
  }

  public static TimetableCell toCell(TimetableRequest.UpdateCell request) {
    return new TimetableCell(
      request.getLecture(),
      request.getProfessor(),
      request.getLocation(),
      request.getStartPeriod(),
      request.getEndPeriod(),
      request.getDay(),
      request.getColor()
    );
  }

  public static TimetableResponse.Simple toSimple(Timetable timetable) {
    return new TimetableResponse.Simple(
      timetable.getId(),
      timetable.getName(),
      timetable.getYear(),
      timetable.getSemester()
    );
  }

  public static TimetableResponse.Detail toDetail(Timetable timetable) {
    return new TimetableResponse.Detail(
      timetable.getId(),
      timetable.getName(),
      timetable.getYear(),
      timetable.getSemester(),
      toCells(timetable.getCells())
    );
  }

  private static List<TimetableResponse.Cell> toCells(List<TimetableCell> cells) {
    return IntStream.range(0, cells.size())
      .mapToObj(index -> {
        TimetableCell cell = cells.get(index);
        return new TimetableResponse.Cell(
          index,
          cell.getLectureName(),
          cell.getProfessorName(),
          cell.getColor(),
          cell.getLocation(),
          cell.getDay(),
          cell.getStartPeriod(),
          cell.getEndPeriod());
      })
      .toList();
  }
}
