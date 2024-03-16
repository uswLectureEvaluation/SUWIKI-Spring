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
public class EvaluatePostReport {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private Long evaluateIdx;

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

  private EvaluatePostReport(Long evaluateIdx, Long reportedUserIdx, Long reportingUserIdx, String professor, String lectureName, String content, LocalDateTime reportedDate) {
    this.evaluateIdx = evaluateIdx;
    this.reportedUserIdx = reportedUserIdx;
    this.reportingUserIdx = reportingUserIdx;
    this.professor = professor;
    this.lectureName = lectureName;
    this.content = content;
    this.reportedDate = reportedDate;
  }

  public static EvaluatePostReport of(Long evaluateIdx, Long reportedUserIdx, Long reportingUserIdx, String professor, String lectureName, String content) {
    return new EvaluatePostReport(evaluateIdx, reportedUserIdx, reportingUserIdx, professor, lectureName, content, LocalDateTime.now());
  }
}
