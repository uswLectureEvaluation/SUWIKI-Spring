package usw.suwiki.global.util.loadjson;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JSONLectureVO {
    private static final String[] KEYWORDS_TO_REMOVE = {"재수강", "비대면수업", "대면수업", "혼합수업"};

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
    public JSONLectureVO(String selectedSemester, String placeSchedule, String professor, String lectureType,
                         String lectureCode,
                         String lectureName, String evaluateType, String diclNo, String majorType, double point,
                         String capprType, int grade) {



        this.selectedSemester = selectedSemester;
        this.placeSchedule = placeSchedule;
        // TODO fix: 그냥 null값을 넣는게..
        this.professor = professor.isEmpty() ? "-" : professor;     //professor 없으면 "-" 로 채움 (null 값 들어가지 않게)
        this.lectureType = resolveDirtyMajorType(majorType);
        this.lectureCode = lectureCode;
        this.lectureName = resolveDirtyLectureName(lectureName);
        this.evaluateType = evaluateType;
        this.diclNo = diclNo;
        this.majorType = majorType;
        this.point = point;
        this.capprType = capprType;
        this.grade = grade;
    }

    // 이상한 강의명 분기 처리 로직.
    // TODO think: 괄호만 없애면 되는거 아닌가? 진욱이한테 JSON 파일 받고 다시 생각
    private String resolveDirtyLectureName(String lectureName) {
        if (lectureName.contains("재수강-")) {
            int index = lectureName.indexOf("(");
            lectureName = lectureName.substring(0, index);
        }

        for (String keyword : KEYWORDS_TO_REMOVE) {
            if (lectureName.contains(keyword)) {
                lectureName = removeWordAndSurrounds(lectureName, keyword);
            }
        }

        return lectureName;
    }

    //"·" to replace "-"
    private String resolveDirtyMajorType(String majorType) {
        if (majorType.contains("·")) {
            return majorType.replace("·", "-");
        }
        return majorType;
    }

    private String removeWordAndSurrounds(String target, String word) {
        // 더 까다로운 순서대로 정렬되어있어야 합니다.
        String[] patternsToRemove = {"(" + word + ")", word + "-", word + "_", word};
        return replaceByPatterns(target, patternsToRemove);
    }

    private String replaceByPatterns(String target, String[] patternsToRemove) {
        for (String pattern : patternsToRemove) {
            if (target.contains(pattern)) {
                return target.replace(pattern, "");
            }
        }
        return target;
    }
}