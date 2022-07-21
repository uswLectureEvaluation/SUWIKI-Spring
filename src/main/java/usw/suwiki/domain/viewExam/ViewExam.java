package usw.suwiki.domain.viewExam;

import lombok.*;
import usw.suwiki.global.BaseTimeEntity;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.user.User;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class ViewExam extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Auto Increment

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_idx")
//    private User user;

    private Long userIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    public void setUserInViewExam(Long userIdx) {
        this.userIdx = userIdx;
    }

    public void setLectureInViewExam(Lecture lecture) {
        this.lecture = lecture;
    }
}
