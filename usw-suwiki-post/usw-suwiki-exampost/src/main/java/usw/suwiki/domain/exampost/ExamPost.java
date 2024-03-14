package usw.suwiki.domain.exampost;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.infra.jpa.BaseTimeEntity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExamPost extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long userId;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Embedded
  private LectureInfo lectureInfo;

  @Embedded
  private ExamDetail examDetail;

  @Builder
  private ExamPost(Long userId, String content, LectureInfo lectureInfo, ExamDetail examDetail) {
    this.userId = userId;
    this.content = content;
    this.lectureInfo = lectureInfo;
    this.examDetail = examDetail;
  }

  public void update(String content, String selectedSemester, ExamDetail examDetail) {
    this.content = content;
    this.lectureInfo.updateSemester(selectedSemester);
    this.examDetail = examDetail;
  }

  public Long getLectureId() {
    return lectureInfo.getLectureId();
  }

  public String getLectureName() {
    return lectureInfo.getLectureName();
  }

  public String getProfessor() {
    return lectureInfo.getProfessor();
  }

  public String getSelectedSemester() {
    return lectureInfo.getSelectedSemester();
  }

  public String getExamType() {
    return examDetail.getExamType();
  }

  public String getExamInfo() {
    return examDetail.getExamInfo();
  }

  public String getExamDifficulty() {
    return examDetail.getExamDifficulty();
  }
}
