package usw.suwiki.global.util.loadjson;

import lombok.Getter;
import org.json.simple.JSONObject;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.domain.LectureDetail;

@Getter
public class JSONLectureVO {
    private final String selectedSemester;
    private final String placeSchedule;
    private final String professor;
    private final int grade;
    private final String lectureType;
    private final String lectureCode;
    private final String lectureName;
    private final String evaluateType;
    private final String diclNo;
    private final String majorType;
    private final double point;
    private final String capprType;

    public JSONLectureVO(JSONObject jsonObject) {
        this.selectedSemester = USWTermResolver.getSemester(jsonObject);
        this.placeSchedule = USWTermResolver.extractPlaceSchedule(jsonObject);
        this.professor = USWTermResolver.getOptionalProfessorName(jsonObject);
        this.lectureType = USWTermResolver.extractLectureFacultyType(jsonObject);
        this.lectureCode = USWTermResolver.extractLectureCode(jsonObject);
        this.lectureName = USWTermResolver.getLectureName(jsonObject);
        this.evaluateType = USWTermResolver.extractEvaluationType(jsonObject);
        this.diclNo = USWTermResolver.extractDivideClassNumber(jsonObject);
        this.majorType = USWTermResolver.getMajorType(jsonObject);
        this.point = USWTermResolver.extractLecturePoint(jsonObject);
        this.capprType = USWTermResolver.extractCapacityType(jsonObject);
        this.grade = USWTermResolver.extractTargetGrade(jsonObject);
    }

    public Lecture toEntity() {
        LectureDetail lectureDetail = LectureDetail.builder()
                .code(lectureCode)
                .grade(grade)
                .point(point)
                .diclNo(diclNo)
                .evaluateType(evaluateType)
                .placeSchedule(placeSchedule)
                .capprType(capprType)
                .build();

        return Lecture.builder()
                .name(lectureName)
                .type(lectureType)
                .professor(professor)
                .semester(selectedSemester)
                .majorType(majorType)
                .lectureDetail(lectureDetail)
                .build();
    }

}