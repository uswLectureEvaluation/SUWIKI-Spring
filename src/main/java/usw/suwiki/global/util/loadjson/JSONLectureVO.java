package usw.suwiki.global.util.loadjson;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.domain.LectureDetail;
import usw.suwiki.domain.lecture.domain.LectureSchedule;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
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

    public static JSONLectureVO from(JSONObject jsonObject) {
        return JSONLectureVO.builder()
                .selectedSemester(USWTermResolver.getSemester(jsonObject))
                .placeSchedule(USWTermResolver.extractPlaceSchedule(jsonObject))
                .professor(USWTermResolver.getOptionalProfessorName(jsonObject))
                .lectureType(USWTermResolver.extractLectureFacultyType(jsonObject))
                .lectureCode(USWTermResolver.extractLectureCode(jsonObject))
                .lectureName(USWTermResolver.getLectureName(jsonObject))
                .evaluateType(USWTermResolver.extractEvaluationType(jsonObject))
                .diclNo(USWTermResolver.extractDivideClassNumber(jsonObject))
                .majorType(USWTermResolver.getMajorType(jsonObject))
                .point(USWTermResolver.extractLecturePoint(jsonObject))
                .capprType(USWTermResolver.extractCapacityType(jsonObject))
                .grade(USWTermResolver.extractTargetGrade(jsonObject))
                .build();
    }

    public Lecture toEntity() {
        LectureDetail lectureDetail = LectureDetail.builder()
                .code(lectureCode)
                .grade(grade)
                .point(point)
                .diclNo(diclNo)
                .evaluateType(evaluateType)
                .capprType(capprType)
                .build();

        // TODO fix: LectureSchedule 생성. 이 메서드 말고 밖에서 만들어야 할듯

        return Lecture.builder()
                .name(lectureName)
                .type(lectureType)
                .professor(professor)
                .semester(selectedSemester)
                .majorType(majorType)
                .lectureDetail(lectureDetail)
                .build();
    }

    public boolean isLectureAndPlaceScheduleEqual(LectureSchedule lectureSchedule) {
        return lectureSchedule.getPlaceSchedule().contains(placeSchedule)
                && lectureSchedule.getLecture().getName().equals(lectureName)
                && lectureSchedule.getLecture().getProfessor().equals(professor)
                && lectureSchedule.getLecture().getMajorType().equals(majorType);
    }

}