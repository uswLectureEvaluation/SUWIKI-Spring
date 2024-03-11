package usw.suwiki.report.exampost;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.exampost.ExamPost;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExamPostReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 신고당한 게시글 id
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

    public static ExamPostReport buildExamPostReport(
      Long examIdx,
      ExamPost examPost,
      Long reportedUserIdx,
      Long reportingUserIdx
    ) {
        return builder()
            .examIdx(examIdx)
            .lectureName(examPost.getLectureName())
            .professor(examPost.getProfessor())
            .content(examPost.getContent())
            .reportedUserIdx(reportedUserIdx)
            .reportingUserIdx(reportingUserIdx)
            .reportedDate(LocalDateTime.now())
            .build();
    }
}
