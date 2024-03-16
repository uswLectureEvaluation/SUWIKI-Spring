package usw.suwiki.domain.report;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExamPostReport {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private Long examIdx;

  @Column
  private Long reportedUserIdx;

  @Column
  private Long reportingUserIdx;

  @Column
  private String professor;

  @Column
  private String lectureName;

  @Column
  private String content;

  @Column
  private LocalDateTime reportedDate;

  private ExamPostReport(Long examIdx, Long reportedUserIdx, Long reportingUserIdx, String professor, String lectureName, String content, LocalDateTime reportedDate) {
    this.examIdx = examIdx;
    this.reportedUserIdx = reportedUserIdx;
    this.reportingUserIdx = reportingUserIdx;
    this.professor = professor;
    this.lectureName = lectureName;
    this.content = content;
    this.reportedDate = reportedDate;
  }

  public static ExamPostReport of(Long examIdx, Long reportedUserIdx, Long reportingUserIdx, String professor, String lectureName, String content) {
    return new ExamPostReport(examIdx, reportedUserIdx, reportingUserIdx, professor, lectureName, content, LocalDateTime.now());
  }
}
