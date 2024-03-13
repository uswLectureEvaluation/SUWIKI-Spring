package usw.suwiki.domain.evaluatepost;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import usw.suwiki.domain.evaluatepost.dto.EvaluatePostSaveDto;
import usw.suwiki.domain.evaluatepost.dto.EvaluatePostUpdateDto;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.user.User;
import usw.suwiki.infra.jpa.BaseTimeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EvaluatePost extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String lectureName;
    private String selectedSemester;
    private String professor;
    private float satisfaction;
    private float learning;
    private float honey;
    private float totalAvg;
    private int team;
    private int difficulty;
    private int homework;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

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


    /**
     * 연관관계 편의 메서드
     */

    public void associateUser(User user) {
        if (Objects.nonNull(this.user)) {
            this.user.removeEvaluatePost(this);
        }
        this.user = user;
        user.addEvaluatePost(this);
    }

    public void associateLecture(Lecture lecture) {
        if (Objects.nonNull(this.lecture)) {
            this.lecture.removeEvaluatePost(this);
        }
        this.lecture = lecture;
        lecture.addEvaluatePost(this);
    }
}
