package usw.suwiki.domain.lecture.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LectureDetailResponseDto {

    private Long id;
    private String semesterList;
    private String professor;
    private String lectureType;
    private String lectureName;
    private String majorType;
    private float lectureTotalAvg;
    private float lectureSatisfactionAvg;
    private float lectureHoneyAvg;
    private float lectureLearningAvg;
    private float lectureTeamAvg;
    private float lectureDifficultyAvg;
    private float lectureHomeworkAvg;

    public LectureDetailResponseDto(Lecture entity) {
        this.id = entity.getId();
        this.semesterList = entity.getSemester();
        this.professor = entity.getProfessor();
        this.lectureType = entity.getType();
        this.lectureName = entity.getName();
        this.majorType = entity.getMajorType();
        this.lectureTotalAvg = entity.getLectureEvaluationInfo().getLectureTotalAvg();
        this.lectureSatisfactionAvg = entity.getLectureEvaluationInfo().getLectureSatisfactionAvg();
        this.lectureHoneyAvg = entity.getLectureEvaluationInfo().getLectureHoneyAvg();
        this.lectureLearningAvg = entity.getLectureEvaluationInfo().getLectureLearningAvg();
        this.lectureTeamAvg = entity.getLectureEvaluationInfo().getLectureTeamAvg();
        this.lectureDifficultyAvg = entity.getLectureEvaluationInfo().getLectureDifficultyAvg();
        this.lectureHomeworkAvg = entity.getLectureEvaluationInfo().getLectureHomeworkAvg();
    }
}
