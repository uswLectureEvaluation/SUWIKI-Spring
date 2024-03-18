package usw.suwiki.domain.lecture;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.lecture.model.Evaluation;
import usw.suwiki.infra.jpa.BaseTimeEntity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.regex.Pattern;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "semester_list")
  private String semester;

  private String professor;

  @Column(name = "lecture_name")
  private String name;

  @Column(name = "major_type")
  private String majorType;

  @Column(name = "lecture_type")
  private String type;

  @Embedded
  private LectureEvaluationInfo lectureEvaluationInfo;

  @Embedded
  private LectureDetail lectureDetail;

  private int postsCount = 0;

  @Builder
  public Lecture(
    String semester,
    String professor,
    String name,
    String majorType,
    String type,
    LectureDetail lectureDetail
  ) {
    this.semester = semester;
    this.professor = professor;
    this.name = name;
    this.majorType = majorType;
    this.type = type;
    this.lectureDetail = lectureDetail;
    this.lectureEvaluationInfo = new LectureEvaluationInfo();
  }

  public void evaluate(Evaluation evaluation) {
    this.lectureEvaluationInfo.apply(evaluation);
    this.lectureEvaluationInfo.calculateAverage(this.postsCount);
    this.postsCount += 1;
  }

  public void updateEvaluation(Evaluation current, Evaluation update) {
    this.lectureEvaluationInfo.cancel(current);
    this.lectureEvaluationInfo.apply(update);
    this.lectureEvaluationInfo.calculateAverage(this.postsCount);
  }

  public int getGrade() {
    return this.lectureDetail.getGrade();
  }

  @Deprecated
  public boolean isOld() {
    return this.semester.length() > 9;
  }

  /**
   * 이하 로직들은 Lecture가 가져야할 비지니스 로직이 아니다. 도메인 서비스로 분리할 것.
   */
  public void addSemester(String singleSemester) {
    validateSingleSemester(singleSemester);
    if (this.semester.isEmpty() || this.semester.contains(singleSemester)) {
      return;
    }

    this.semester = extendSemester(this.semester, singleSemester);
  }

  public void removeSemester(String singleSemester) {
    validateSingleSemester(singleSemester);
    if (this.semester.contains(singleSemester)) {
      this.semester = this.semester.replace(buildAddedSingleSemester(singleSemester), "");
    }
  }

  private void validateSingleSemester(String candidate) {
    boolean matches = Pattern.matches("^(2\\d{3})-(1|2)$", candidate); // todo: 패턴 객체는 너무 비싸서 caching해서 사용해야함.
    if (!matches) {
      throw new IllegalArgumentException("invalid semester");
    }
  }

  private static String extendSemester(String originalSemesters, String semester) {
    return originalSemesters + buildAddedSingleSemester(semester);
  }

  private static String buildAddedSingleSemester(String semester) {
    return ", " + semester;
  }
}
