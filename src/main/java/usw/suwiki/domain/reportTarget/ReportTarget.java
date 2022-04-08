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

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user; //userIdx -- 신고 한 사람

    @ManyToOne
    @JoinColumn(name = "evaluate_idx")
    private EvaluatePosts evaluatePosts; //EvaluateIdx

    @ManyToOne
    @JoinColumn(name = "exam_idx")
    private ExamPosts examPosts; //ExamIdx

    @Column
    private boolean postType; //true = evaluate, false = exam

    @Column
    private String comment;

    @Column
    private LocalDateTime reportedDate;

//    @Column
//    private String admin;         : 프론트에서 처리

}
