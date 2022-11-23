package usw.suwiki.domain.evaluation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.user.User;
import usw.suwiki.global.BaseTimeEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class EvaluatePosts extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //autoIncrement
    private Long id;

    private String lectureName; //과목
    private String selectedSemester;
    private String professor;   //교수

    private float satisfaction;    //수업 만족도
    private float learning; //배움지수
    private float honey;    //꿀강지수
    private float totalAvg;   // 평균지수

    private int team;    //조모임 횟수
    private int difficulty;   //성적비율
    private int homework;   //과제량

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    @LastModifiedDate // 조회한 Entity값을 변경할때 ,시간이 자동 저장된다.
    private LocalDateTime modifiedDate;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;    //주관적인 강의평가 입력내용

    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public EvaluatePosts(EvaluatePostsSaveDto dto) {
        this.lectureName = dto.getLectureName();
        this.selectedSemester = dto.getSelectedSemester();
        this.professor = dto.getProfessor();
        this.satisfaction = dto.getSatisfaction();
        this.learning = dto.getLearning();
        this.honey = dto.getHoney();
        this.team = dto.getTeam();
        this.difficulty = dto.getDifficulty();
        this.homework = dto.getHomework();
        this.content = dto.getContent();
        this.totalAvg = (learning + honey + satisfaction) / 3;
    }

    public void update(EvaluatePostsUpdateDto dto) {
        this.selectedSemester = dto.getSelectedSemester();
        this.satisfaction = dto.getSatisfaction();
        this.learning = dto.getLearning();
        this.honey = dto.getHoney();
        this.team = dto.getTeam();
        this.difficulty = dto.getDifficulty();
        this.homework = dto.getHomework();
        this.content = dto.getContent();
        this.totalAvg = (learning + honey + satisfaction) / 3;
    }
}
