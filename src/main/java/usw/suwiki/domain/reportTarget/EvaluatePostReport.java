package usw.suwiki.domain.reportTarget;

import lombok.*;
import usw.suwiki.domain.evaluation.EvaluatePosts;
import usw.suwiki.domain.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Builder @Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluatePostReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Auto Increment

    // 신고당한 게시글 id
    @Column
    private Long evaluateIdx;

    // 신고당한 유저 Id
    private Long reportedUserIdx;

    // 신고한 유저 Id
    private Long reportingUserIdx;

    // 교수이름
    @Column
    private String professor;

    // 과목이름
    @Column
    private String lectureName;

    // 신고 내용
    @Column
    private String content;

    //신고한 날짜
    @Column
    private LocalDateTime reportedDate;

}
