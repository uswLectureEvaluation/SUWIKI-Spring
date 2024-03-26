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

  @Data
  public static class Lectures { // 시간표 선택 시 나오는 강의 데이터 (네이밍 수정 요망)
    private final boolean isLast;
    private final List<Lecture> content;
  }

  @Data
  public static class Lecture {
    private final Long id;
    private final String name;
    private final String type;
    private final String major;
    private final int grade;
    private final String professorName;
    private final List<LectureCell> originalCellList; // todo: 프론트에 빌어서 이름 바꾸기
  }

  @Data
  public static class LectureCell {
    private final String location;
    private final String day;
    private final Integer startPeriod;
    private final Integer endPeriod;
  }
}
