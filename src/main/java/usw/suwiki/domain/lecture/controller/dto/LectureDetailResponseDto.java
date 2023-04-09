package usw.suwiki.domain.lecture.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.lecture.domain.Lecture;

@Getter
@NoArgsConstructor
public class LectureDetailResponseDto {
    private Long id;    //필요한가?

    private String semesterList;
    private String professor;
    private String lectureType; //이수 구분
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
        this.lectureType = entity.getLectureDetail().getType();
        this.lectureName = entity.getName();
        this.majorType = entity.getMajorType();
        this.lectureTotalAvg = entity.getLectureAverage().getLectureTotalAvg();
        this.lectureSatisfactionAvg = entity.getLectureAverage().getLectureSatisfactionAvg();
        this.lectureHoneyAvg = entity.getLectureAverage().getLectureHoneyAvg();
        this.lectureLearningAvg = entity.getLectureAverage().getLectureLearningAvg();
        this.lectureTeamAvg = entity.getLectureAverage().getLectureTeamAvg();
        this.lectureDifficultyAvg = entity.getLectureAverage().getLectureDifficultyAvg();
        this.lectureHomeworkAvg = entity.getLectureAverage().getLectureHomeworkAvg();
    }
}