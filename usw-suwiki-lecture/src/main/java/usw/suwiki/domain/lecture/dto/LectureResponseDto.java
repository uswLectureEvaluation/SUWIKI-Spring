package usw.suwiki.domain.lecture.dto;

import lombok.Getter;
import usw.suwiki.domain.lecture.Lecture;

@Getter
public class LectureResponseDto {
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

    public LectureResponseDto(Lecture lecture) {
        this.id = lecture.getId();
        this.semesterList = lecture.getSemester();
        this.professor = lecture.getProfessor();
        this.lectureType = lecture.getType();
        this.lectureName = lecture.getName();
        this.majorType = lecture.getMajorType();
        this.lectureTotalAvg = lecture.getLectureEvaluationInfo().getLectureTotalAvg();
        this.lectureSatisfactionAvg = lecture.getLectureEvaluationInfo().getLectureSatisfactionAvg();
        this.lectureHoneyAvg = lecture.getLectureEvaluationInfo().getLectureHoneyAvg();
        this.lectureLearningAvg = lecture.getLectureEvaluationInfo().getLectureLearningAvg();
    }
}
