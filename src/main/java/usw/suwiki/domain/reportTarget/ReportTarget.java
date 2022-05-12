package usw.suwiki.domain.reportTarget;

import lombok.*;
import usw.suwiki.domain.evaluation.EvaluatePosts;
import usw.suwiki.domain.exam.ExamPosts;
import usw.suwiki.domain.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Builder @Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Auto Increment

    @Column
    private Long evaluateIdx;

    @Column
    private Long examIdx;

    // 교수이름
    @Column
    private String professor;

    // 과목이름
    @Column
    private String lectureName;

    // true = evaluate, false = exam
    @Column
    private boolean postType;

    // 신고 내용
    @Column
    private String content;

    //신고한 날짜
    @Column
    private LocalDateTime reportedDate;

}
