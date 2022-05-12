package usw.suwiki.dto.lecture;

import usw.suwiki.domain.lecture.Lecture;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        this.semesterList = entity.getSemesterList();
        this.professor = entity.getProfessor();
        this.lectureType = entity.getLectureType();
        this.lectureName = entity.getLectureName();
        this.majorType = entity.getMajorType();
        this.lectureTotalAvg = entity.getLectureTotalAvg();
        this.lectureSatisfactionAvg = entity.getLectureSatisfactionAvg();
        this.lectureHoneyAvg = entity.getLectureHoneyAvg();
        this.lectureLearningAvg = entity.getLectureLearningAvg();
    }
}