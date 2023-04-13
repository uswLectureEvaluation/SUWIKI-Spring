package usw.suwiki.domain.lecture.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.lecture.domain.Lecture;

@Getter
@NoArgsConstructor
public class LectureResponseDto {
    private Long id;

    private String semesterList;
    private String professor;
    private String lectureType; //이수 구분
    private String lectureName;
    private String majorType;

    private float lectureTotalAvg;
    private float lectureSatisfactionAvg;
    private float lectureHoneyAvg;
    private float lectureLearningAvg;

    public LectureResponseDto(Lecture entity) {
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
    }
}