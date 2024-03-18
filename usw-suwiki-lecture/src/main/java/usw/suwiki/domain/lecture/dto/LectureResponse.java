package usw.suwiki.domain.lecture.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LectureResponse {

  @Data
  public static class Simples {
    private final long count;
    private final List<Simple> data;
  }

  @Data
  public static class Simple {
    private final Long id;
    private final String semesterList;
    private final String professor;
    private final String lectureType;
    private final String lectureName;
    private final String majorType;
    private final float lectureTotalAvg;
    private final float lectureSatisfactionAvg;
    private final float lectureHoneyAvg;
    private final float lectureLearningAvg;
  }

  @Data
  public static class Detail {
    private final Long id;
    private final String semesterList;
    private final String professor;
    private final String lectureType;
    private final String lectureName;
    private final String majorType;
    private final float lectureTotalAvg;
    private final float lectureSatisfactionAvg;
    private final float lectureHoneyAvg;
    private final float lectureLearningAvg;
    private final float lectureTeamAvg;
    private final float lectureDifficultyAvg;
    private final float lectureHomeworkAvg;
  }
}
