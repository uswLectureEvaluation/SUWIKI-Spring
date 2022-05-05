package usw.suwiki.domain.lecture;

import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import usw.suwiki.domain.BaseTimeEntity;
import usw.suwiki.domain.evaluation.EvaluatePosts;
import usw.suwiki.dto.evaluate.EvaluatePostsToLecture;
import usw.suwiki.dto.lecture.JsonToLectureDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Lecture extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String semester;
    private String placeSchedule;  // 시간표 대로 나워야 하나?
    private String professor;
    private int grade;
    private String lectureType;
    private String lectureCode;
    private String lectureName;
    private String evaluateType;
    private String diclNo;
    private String majorType;
    private double point;
    private String capprType;

    private float lectureTotalAvg = 0;
    private float lectureSatisfactionAvg = 0;
    private float lectureHoneyAvg = 0;
    private float lectureLearningAvg = 0;
    private float lectureTeamAvg = 0;
    private float lectureDifficultyAvg = 0;
    private float lectureHomeworkAvg = 0;

    private float lectureSatisfactionValue= 0;
    private float lectureHoneyValue = 0;
    private float lectureLearningValue = 0;
    private float lectureTeamValue = 0;
    private float lectureDifficultyValue = 0;
    private float lectureHomeworkValue = 0;
    private int postsCount;

    public void setSemester(String semester) {
        this.semester = semester;
    }

    @Builder
    public Lecture(String semester, String placeSchedule, String professor, String lectureType, String lectureCode,
                   String lectureName, String evaluateType, String diclNo, String majorType, double point, String capprType, int grade) {
        this.semester = semester;
        this.placeSchedule = placeSchedule;
        this.professor = professor;
        this.lectureType = lectureType;
        this.lectureCode = lectureCode;
        this.lectureName = lectureName;
        this.evaluateType = evaluateType;
        this.diclNo = diclNo;
        this.majorType = majorType;
        this.point = point;
        this.capprType = capprType;
        this.grade = grade;
    }


    public void addLectureValue(EvaluatePostsToLecture dto){
        this.lectureSatisfactionValue += dto.getLectureSatisfaction();
        this.lectureHoneyValue += dto.getLectureHoney();
        this.lectureLearningValue += dto.getLectureLearning();
        this.lectureTeamValue += dto.getLectureTeam();
        this.lectureDifficultyValue += dto.getLectureDifficulty();
        this.lectureHomeworkValue += dto.getLectureHomework();
        this.postsCount += 1;
    }

    public void cancelLectureValue(EvaluatePostsToLecture dto){
        this.lectureSatisfactionValue -= dto.getLectureSatisfaction();
        this.lectureHoneyValue -= dto.getLectureHoney();
        this.lectureLearningValue -= dto.getLectureLearning();
        this.lectureTeamValue -= dto.getLectureTeam();
        this.lectureDifficultyValue -= dto.getLectureDifficulty();
        this.lectureHomeworkValue -= dto.getLectureHomework();
        this.postsCount -= 1;
    }

    public void calcLectureAvg(){
        if(postsCount < 1){
            this.lectureTotalAvg = 0;
            this.lectureSatisfactionAvg = 0;
            this.lectureHoneyAvg = 0;
            this.lectureLearningAvg = 0;
            this.lectureTeamAvg = 0;
            this.lectureDifficultyAvg = 0;
            this.lectureHomeworkAvg = 0;
        }else {
            this.lectureSatisfactionAvg = lectureSatisfactionValue / postsCount;
            this.lectureHoneyAvg = lectureHoneyValue / postsCount;
            this.lectureLearningAvg = lectureLearningValue / postsCount;
            this.lectureTeamAvg = lectureTeamValue / postsCount;
            this.lectureDifficultyAvg = lectureDifficultyValue / postsCount;
            this.lectureHomeworkAvg = lectureHomeworkValue / postsCount;
            this.lectureTotalAvg = (lectureSatisfactionAvg + lectureHoneyAvg + lectureLearningAvg) / 3;
        }
    }


    public void toEntity(JsonToLectureDto dto){
        this.semester = dto.getSemester();
        this.placeSchedule = dto.getPlaceSchedule();
        this.professor = dto.getProfessor();
        this.lectureType = dto.getLectureType();
        this.lectureCode = dto.getLectureCode();
        this.lectureName = dto.getLectureName();
        this.evaluateType = dto.getEvaluateType();
        this.diclNo = dto.getDiclNo();
        this.majorType = dto.getMajorType();
        this.point = dto.getPoint();
        this.capprType = dto.getCapprType();
    }

}