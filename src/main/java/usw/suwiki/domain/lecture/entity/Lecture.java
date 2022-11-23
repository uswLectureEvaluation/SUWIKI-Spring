package usw.suwiki.domain.lecture.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import usw.suwiki.domain.evaluation.EvaluatePostsToLecture;
import usw.suwiki.domain.lecture.dto.JsonToLectureDto;
import usw.suwiki.global.BaseTimeEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class Lecture extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String semesterList;
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

    private float lectureSatisfactionValue = 0;
    private float lectureHoneyValue = 0;
    private float lectureLearningValue = 0;
    private float lectureTeamValue = 0;
    private float lectureDifficultyValue = 0;
    private float lectureHomeworkValue = 0;
    private int postsCount;

    @LastModifiedDate // 조회한 Entity값을 변경할때 ,시간이 자동 저장된다.
    private LocalDateTime modifiedDate;

    public void setSemester(String semester) {
        this.semesterList = semester;
    }


    @Builder
    public Lecture(String semesterList, String placeSchedule, String professor, String lectureType, String lectureCode,
                   String lectureName, String evaluateType, String diclNo, String majorType, double point, String capprType, int grade) {
        this.semesterList = semesterList;
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


    public void addLectureValue(EvaluatePostsToLecture dto) {
        this.lectureSatisfactionValue += dto.getLectureSatisfaction();
        this.lectureHoneyValue += dto.getLectureHoney();
        this.lectureLearningValue += dto.getLectureLearning();
        this.lectureTeamValue += dto.getLectureTeam();
        this.lectureDifficultyValue += dto.getLectureDifficulty();
        this.lectureHomeworkValue += dto.getLectureHomework();
        this.postsCount += 1;
    }

    public void cancelLectureValue(EvaluatePostsToLecture dto) {
        this.lectureSatisfactionValue -= dto.getLectureSatisfaction();
        this.lectureHoneyValue -= dto.getLectureHoney();
        this.lectureLearningValue -= dto.getLectureLearning();
        this.lectureTeamValue -= dto.getLectureTeam();
        this.lectureDifficultyValue -= dto.getLectureDifficulty();
        this.lectureHomeworkValue -= dto.getLectureHomework();
        this.postsCount -= 1;
    }

    public void calcLectureAvg() {
        if (postsCount < 1) {
            this.lectureTotalAvg = 0;
            this.lectureSatisfactionAvg = 0;
            this.lectureHoneyAvg = 0;
            this.lectureLearningAvg = 0;
            this.lectureTeamAvg = 0;
            this.lectureDifficultyAvg = 0;
            this.lectureHomeworkAvg = 0;
        } else {
            this.lectureSatisfactionAvg = lectureSatisfactionValue / postsCount;
            this.lectureHoneyAvg = lectureHoneyValue / postsCount;
            this.lectureLearningAvg = lectureLearningValue / postsCount;
            this.lectureTeamAvg = lectureTeamValue / postsCount;
            this.lectureDifficultyAvg = lectureDifficultyValue / postsCount;
            this.lectureHomeworkAvg = lectureHomeworkValue / postsCount;
            this.lectureTotalAvg = (lectureSatisfactionAvg + lectureHoneyAvg + lectureLearningAvg) / 3;
        }
    }


    public void toEntity(JsonToLectureDto dto) {
        this.semesterList = dto.getSelectedSemester();
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