package usw.suwiki.report.evaluatepost;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.evaluatepost.domain.EvaluatePost;
import usw.suwiki.domain.user.user.controller.dto.UserRequestDto.EvaluateReportForm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;


@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class EvaluatePostReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 신고당한 게시글 id
    @Column
    private Long evaluateIdx;

    private Long reportedUserIdx;

    private Long reportingUserIdx;

    @Column
    private String professor;

    @Column
    private String lectureName;

    @Column
    private String content;

    @Column
    private LocalDateTime reportedDate;


    public static EvaluatePostReport buildEvaluatePostReport(
        EvaluateReportForm evaluateReportForm,
        EvaluatePost evaluatePost,
        Long reportedUserIdx,
        Long reportingUserIdx
    ) {
        return builder()
            .evaluateIdx(evaluateReportForm.evaluateIdx())
            .lectureName(evaluatePost.getLectureName())
            .professor(evaluatePost.getProfessor())
            .content(evaluatePost.getContent())
            .reportedUserIdx(reportedUserIdx)
            .reportingUserIdx(reportingUserIdx)
            .reportedDate(LocalDateTime.now())
            .build();
    }
}
