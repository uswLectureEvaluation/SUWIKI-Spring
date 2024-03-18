package usw.suwiki.domain.lecture.schedule.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.TimetableException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static usw.suwiki.domain.lecture.dto.LectureResponse.LectureCell;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LectureStringConverter {
  private static final String LOCATION_NOT_SETTLED = "미정";
  private static final int SPLIT_SIZE = 3;

  /**
   * @param placeSchedule 강의 장소 및 시간 원본 lecture_schedule.place_schedule
   * @implNote place_schedule을 DTO 리스트로 변환
   */
  public static List<LectureCell> chunkToLectureCells(String placeSchedule) {
    List<LectureCell> lectureCells = new ArrayList<>();

    // e.g. "IT103(..),IT505(..)" -> [ "IT103(..)", "IT505(..)" ]
    for (String locationAndDays : placeSchedule.split(",(?![^()]*\\))")) {
      String location = extractLocationFromLocationAndDays(locationAndDays);
      String dayAndPeriodsString = extractDaysFromLocationAndDays(locationAndDays);

      // e.g. "월1,2, 화1,2" -> [ "월1,2", "화1,2" ]
      for (String dayAndPeriods : dayAndPeriodsString.split(" ")) {
        String day = dayAndPeriods.substring(0, 1);
        String stringPeriods = dayAndPeriods.substring(1);

        for (List<Integer> periods : splitPeriodsIntoThree(stringPeriods)) {
          LectureCell cell = new LectureCell(
            location,
            toEnglish(day),
            periods.get(0),
            periods.get(periods.size() - 1)
          );

          lectureCells.add(cell);
        }
      }
    }

    return lectureCells;
  }

  // e.g. "IT103(월1,2, 화1,2)" -> "IT103"
  private static String extractLocationFromLocationAndDays(String locationAndDays) {
    String location = locationAndDays.split("\\(")[0];
    return location.isBlank() ? LOCATION_NOT_SETTLED : location;
  }

  // e.g. IT103(월1,2, 화1,2) -> "월1,2, 화1,2"
  private static String extractDaysFromLocationAndDays(String locationAndDays) {
    int start = locationAndDays.indexOf('(') + 1;
    int end = locationAndDays.lastIndexOf(')');
    return locationAndDays.substring(start, end);
  }

  // e.g. "1,2,3,5,6,7,9,10" -> [ [1,2,3], [5,6,7], [9,10] ]
  private static List<List<Integer>> splitPeriodsIntoThree(String unconnectedPeriods) {
    List<Integer> sortedPeriods = Arrays.stream(unconnectedPeriods.split(","))
      .map(Integer::parseInt)
      .sorted()
      .toList();

    return Stream.iterate(0, index -> index + SPLIT_SIZE)
      .limit((sortedPeriods.size() + 2) / SPLIT_SIZE)
      .map(current -> sortedPeriods.subList(current, Math.min(current + SPLIT_SIZE, sortedPeriods.size())))
      .toList();
  }

  private static String toEnglish(String korean) {
    return switch (korean) {
      case "월" -> "MON";
      case "화" -> "TUE";
      case "수" -> "WED";
      case "목" -> "THU";
      case "금" -> "FRI";
      case "토" -> "SAT";
      case "일" -> "SUN";

      default -> throw new TimetableException(ExceptionType.INVALID_TIMETABLE_CELL_DAY); // todo: 삭제할 것
    };
  }
}
