package usw.suwiki.domain.evaluatepost.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import usw.suwiki.domain.evaluatepost.controller.dto.EvaluatePostSaveDto;
import usw.suwiki.domain.evaluatepost.controller.dto.EvaluatePostUpdateDto;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.global.BaseTimeEntity;

@Getter
@NoArgsConstructor
@Entity
public class EvaluatePost extends BaseTimeEntity {

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

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;    //주관적인 강의평가 입력내용

    // TODO: 연관관계 편의 메서드로 대체
    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Builder
    public EvaluatePost(
            String lectureName,
            String selectedSemester,
            String professor,
            String content,
            float satisfaction,
            float learning,
            float honey,
            float totalAvg,
            int team,
            int difficulty,
            int homework
    ) {
        this.lectureName = lectureName;
        this.selectedSemester = selectedSemester;
        this.professor = professor;
        this.satisfaction = satisfaction;
        this.learning = learning;
        this.honey = honey;
        this.totalAvg = totalAvg;
        this.team = team;
        this.difficulty = difficulty;
        this.homework = homework;
        this.content = content;
    }

    public EvaluatePost(EvaluatePostSaveDto dto) {  // TODO: 삭제 -> 생성자로 DTO측에서 만들기. 엔티티에선 DTO를 모르는게 좋다.
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
        this.totalAvg = (learning + honey + satisfaction) / 3;  // TODO: 해당 로직 따로 관리. 평균 유틸 메서드 찾아보자
    }

    public void update(EvaluatePostUpdateDto dto) {
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


    // 연관관계 편의 메서드
    public void associateUser(User user) {
        if (Objects.nonNull(this.user)) {
            this.user.removeEvaluatePost(this);
        }
        this.user = user;
        user.addEvaluatePost(this);
    }

}
