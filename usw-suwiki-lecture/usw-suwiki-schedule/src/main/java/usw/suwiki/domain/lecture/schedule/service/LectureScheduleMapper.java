package usw.suwiki.domain.lecture.schedule.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.lecture.dto.LectureResponse;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LectureScheduleMapper {

  public static LectureResponse.Lecture toEmptyCellResponse(Lecture lecture) {
    return map(lecture, Collections.emptyList());
  }

  public static LectureResponse.Lecture toResponse(Lecture lecture, List<LectureResponse.LectureCell> cells) {
    return map(lecture, cells);
  }

  private static LectureResponse.Lecture map(Lecture lecture, List<LectureResponse.LectureCell> lectureCells) {
    return new LectureResponse.Lecture(
      lecture.getId(),
      lecture.getName(),
      lecture.getType(),
      lecture.getMajorType(),
      lecture.getGrade(),
      lecture.getProfessor(),
      lectureCells
    );
  }
}
