package usw.suwiki.domain.lecture.data;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.lecture.LectureDetail;
import usw.suwiki.domain.lecture.schedule.LectureSchedule;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JSONLectureVO { // todo: vo..?
    private final String selectedSemester;
    private final String placeSchedule;
    private final String professor;
    private final int grade;
    private final String lectureType;
    private final String lectureCode;
    private final String lectureName;
    private final String evaluateType;
    private final String dividedClassNumber;
    private final String majorType;
    private final double point;
    private final String capacityPresentationType;

    public static JSONLectureVO from(JSONObject jsonObject) {
        return builder()
            .selectedSemester(USWTermResolver.getSemester(jsonObject))
            .placeSchedule(USWTermResolver.extractPlaceSchedule(jsonObject))
            .professor(USWTermResolver.getOptionalProfessorName(jsonObject))
            .lectureType(USWTermResolver.extractLectureFacultyType(jsonObject))
            .lectureCode(USWTermResolver.extractLectureCode(jsonObject))
            .lectureName(USWTermResolver.getLectureName(jsonObject))
            .evaluateType(USWTermResolver.extractEvaluationType(jsonObject))
            .dividedClassNumber(USWTermResolver.extractDivideClassNumber(jsonObject))
            .majorType(USWTermResolver.getMajorType(jsonObject))
            .point(USWTermResolver.extractLecturePoint(jsonObject))
            .capacityPresentationType(USWTermResolver.extractCapacityType(jsonObject))
            .grade(USWTermResolver.extractTargetGrade(jsonObject))
            .build();
    }

    public Lecture toEntity() {
        LectureDetail lectureDetail = LectureDetail.builder()
            .code(lectureCode)
            .grade(grade)
            .point(point)
            .diclNo(dividedClassNumber)
            .evaluateType(evaluateType)
            .capprType(capacityPresentationType)
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


    public boolean isPlaceScheduleValid() {
        return !(placeSchedule.equals("null") || placeSchedule.isEmpty());
    }

    public boolean isLectureEqual(Lecture lecture) { // todo: 주객전도
        return lecture.getName().equals(lectureName)
            && lecture.getProfessor().equals(professor)
            && lecture.getMajorType().equals(majorType)
            && lecture.getLectureDetail().getDiclNo().equals(dividedClassNumber);
    }

    public boolean isLectureAndPlaceScheduleEqual(LectureSchedule lectureSchedule) {
        return isLectureEqual(lectureSchedule.getLecture()) && lectureSchedule.getPlaceSchedule().contains(placeSchedule); // todo: 디미터의 법칙
    }
}
