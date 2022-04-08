package usw.suwiki.dto.lecture;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JsonToLectureDto {
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

    @Builder
    public JsonToLectureDto(String semester, String placeSchedule, String professor, String lectureType, String lectureCode,
                            String lectureName, String evaluateType, String diclNo, String majorType, double point,
                            String capprType , int grade) {
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
}