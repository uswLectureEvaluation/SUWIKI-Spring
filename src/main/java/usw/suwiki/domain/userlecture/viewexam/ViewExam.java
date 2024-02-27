package usw.suwiki.domain.userlecture.viewexam;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.global.BaseTimeEntity;

@Getter
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
public class ViewExam extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @Builder
    public ViewExam(User user, Lecture lecture) {
        this.user = user;
        this.lecture = lecture;
    }

    public void setUserInViewExam(User user) {
        this.user = user;
    }

    public void setLectureInViewExam(Lecture lecture) {
        this.lecture = lecture;
    }
}
