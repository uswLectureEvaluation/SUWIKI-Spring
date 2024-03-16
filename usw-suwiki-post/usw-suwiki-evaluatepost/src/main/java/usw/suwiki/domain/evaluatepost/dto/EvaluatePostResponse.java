package usw.suwiki.domain.evaluatepost.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EvaluatePostResponse {

  @Data
  public static class Details {
    private final List<Detail> data;
    private final boolean isWritten;
  }

  @Getter
  public static class Detail {
    private final Long id;
    private final String content;
    private final String selectedSemester;
    private final float totalAvg;
    private final float satisfaction;
    private final float learning;
    private final float honey;
    private final int team;
    private final int difficulty;
    private final int homework;

    @QueryProjection
    public Detail(Long id, String content, String selectedSemester, float totalAvg, float satisfaction, float learning, float honey, int team, int difficulty, int homework) {
      this.id = id;
      this.content = content;
      this.selectedSemester = selectedSemester;
      this.totalAvg = totalAvg;
      this.satisfaction = satisfaction;
      this.learning = learning;
      this.honey = honey;
      this.team = team;
      this.difficulty = difficulty;
      this.homework = homework;
    }
  }

  @Getter
  public static class MyPost {
    private final Long id;
    private final String content;
    private final String lectureName;
    private final String professor;
    private final String majorType;
    private final String selectedSemester;
    private final String semesterList;
    private final float totalAvg;
    private final float satisfaction;
    private final float learning;
    private final float honey;
    private final int team;
    private final int difficulty;
    private final int homework;

    @QueryProjection
    public MyPost(Long id, String content, String lectureName, String professor, String majorType, String selectedSemester, String semesterList, float totalAvg, float satisfaction, float learning, float honey, int team, int difficulty, int homework) {
      this.id = id;
      this.content = content;
      this.lectureName = lectureName;
      this.professor = professor;
      this.majorType = majorType;
      this.selectedSemester = selectedSemester;
      this.semesterList = semesterList;
      this.totalAvg = totalAvg;
      this.satisfaction = satisfaction;
      this.learning = learning;
      this.honey = honey;
      this.team = team;
      this.difficulty = difficulty;
      this.homework = homework;
    }
  }
}
