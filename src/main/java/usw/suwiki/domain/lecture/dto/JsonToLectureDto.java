package usw.suwiki.domain.lecture.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JsonToLectureDto {
    private String selectedSemester;
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

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public void setMajorType(String majorType) {
        this.majorType = majorType;
    }

    public void setLectureName(String lectureName) {
        this.lectureName = lectureName;
    }

    @Builder
    public JsonToLectureDto(String selectedSemester, String placeSchedule, String professor, String lectureType, String lectureCode,
                            String lectureName, String evaluateType, String diclNo, String majorType, double point,
                            String capprType, int grade) {
        this.selectedSemester = selectedSemester;
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
}