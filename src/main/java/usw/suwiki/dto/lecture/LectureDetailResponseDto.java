package usw.suwiki.dto.lecture;

import usw.suwiki.domain.lecture.Lecture;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LectureDetailResponseDto {
    private Long id;    //필요한가?

    private String selectedSemester;
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
        this.selectedSemester = entity.getSemester();
        this.professor = entity.getProfessor();
        this.lectureType = entity.getLectureType();
        this.lectureName = entity.getLectureName();
        this.majorType = entity.getMajorType();
        this.lectureTotalAvg = entity.getLectureTotalAvg();
        this.lectureSatisfactionAvg = entity.getLectureSatisfactionAvg();
        this.lectureHoneyAvg = entity.getLectureHoneyAvg();
        this.lectureLearningAvg = entity.getLectureLearningAvg();
        this.lectureTeamAvg = entity.getLectureTeamAvg();
        this.lectureDifficultyAvg = entity.getLectureDifficultyAvg();
        this.lectureHomeworkAvg = entity.getLectureHomeworkAvg();
    }
}