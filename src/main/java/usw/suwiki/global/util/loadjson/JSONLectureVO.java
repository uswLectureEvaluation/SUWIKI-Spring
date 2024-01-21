package usw.suwiki.global.util.loadjson;

import lombok.Getter;
import org.json.simple.JSONObject;

@Getter
public class JSONLectureVO {
    private static final String[] KEYWORDS_TO_REMOVE = {"재수강", "비대면수업", "대면수업", "혼합수업"};

    private final String selectedSemester;
    private final String placeSchedule;  // 시간표 대로 나워야 하나?
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
        String selectedSemester = jsonObject.get("subjtEstbYear")
                + "-"
                + String.valueOf(jsonObject.get("subjtEstbSmrCd")).charAt(0);
        String placeSchedule = String.valueOf(jsonObject.get("timtSmryCn"));
        String professor = String.valueOf(jsonObject.get("reprPrfsEnoNm"));
        String lectureType = (String) jsonObject.get("facDvnm");
        String lectureCode = (String) jsonObject.get("subjtCd");
        String lectureName = String.valueOf(jsonObject.get("subjtNm"));
        String evaluateType = (String) jsonObject.get("cretEvalNm");
        String diclNo = String.valueOf(jsonObject.get("diclNo"));
        String majorType = String.valueOf(jsonObject.get("estbDpmjNm"));
        double point = Double.parseDouble(String.valueOf(jsonObject.get("point")));
        String capprType = (String) jsonObject.get("capprTypeNm");
        int grade = Integer.parseInt(jsonObject.get("trgtGrdeCd").toString());

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