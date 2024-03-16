package usw.suwiki.domain.exampost.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExamPostResponse {

  @Getter
  @AllArgsConstructor
  public static class Details {
    private List<Detail> data;
    private boolean canRead;
    private final boolean isExamDataExist;
    private final boolean isWritten;

    public static Details withData(List<Detail> data, boolean isWritten) {
      return new Details(data, true, true, isWritten);
    }

    public static Details noData(boolean isWritten) {
      return new Details(Collections.emptyList(), true, false, isWritten);
    }

    public void noAccess() {
      this.data = Collections.emptyList();
      this.canRead = false;
    }
  }

  @Data
  public static class Detail {
    private final Long id;
    private final String content;
    private final String selectedSemester;
    private final String examType;
    private final String examInfo;
    private final String examDifficulty;
  }

  @Getter
  public static class MyPost {
    private final Long id;
    private final String content;
    private final String lectureName;
    private final String selectedSemester;
    private final String professor;
    private final String majorType;
    private final String semesterList;
    private final String examType;
    private final String examInfo;
    private final String examDifficulty;

    @QueryProjection
    public MyPost(Long id, String content, String lectureName, String selectedSemester, String professor, String majorType, String semesterList, String examType, String examInfo, String examDifficulty) {
      this.id = id;
      this.content = content;
      this.lectureName = lectureName;
      this.selectedSemester = selectedSemester;
      this.professor = professor;
      this.majorType = majorType;
      this.semesterList = semesterList;
      this.examType = examType;
      this.examInfo = examInfo;
      this.examDifficulty = examDifficulty;
    }
  }
}
