package usw.suwiki.domain.lecture.data;

import org.json.simple.JSONObject;

final class USWTermResolver {

    private static final String[] KEYWORDS_TO_REMOVE = {"재수강", "비대면수업", "대면수업", "혼합수업"};

    // 변환에 필요한 최소한의 스트링 형식입니다.
    // 해당 형식에 맞지 않는다면 수원대측에서 형식을 바꾼 것이므로 더 이상 호환되지 않기 때문에 예외를 발생시키고 적재를 중단해야 합니다.
    // https://regexr.com/7rq1g     "^([\\s가-힣A-Za-z\\d-]+\\([월화수목금토일]\\d+(?:,\\d+)*.*?\\))+$"
    // pass : "강의실107-1(수6,7,8)" "강의실 B215(화5,6,7 수5,6,7)"
    // pass : "(월1,2)" -> "미정(월1,2)"
    // fail : "강의실(1,2)" "강의실 월1,2" "강의실107(요일아님6,7,8)" "요일없음(1,2)" "강의실103(화5,6),강의실103"

    private static final String ESTABLISHED_YEAR = "subjtEstbYear";
    private static final String ESTABLISHED_SEMESTER = "subjtEstbSmrCd";
    private static final String PLACE_SCHEDULE = "timtSmryCn";
    private static final String REPRESENT_PROFESSOR_NAME = "reprPrfsEnoNm";
    private static final String FACULTY_DIVISION = "facDvnm";
    private static final String SUBJECT_CODE = "subjtCd";
    private static final String SUBJECT_NAME = "subjtNm";
    private static final String EVALUATION_TYPE = "cretEvalNm";
    private static final String DIVIDE_CLASS_NUMBER = "diclNo";
    private static final String DEPARTMENT = "estbDpmjNm";
    private static final String POINT = "point";
    private static final String CAPACITY_TYPE = "capprTypeNm";
    private static final String TARGET_GRADE = "trgtGrdeCd";


    public static String getSemester(JSONObject jsonObject) {
        return resolveSemester(extractYear(jsonObject), extractSemester(jsonObject));
    }

    public static String getOptionalProfessorName(JSONObject jsonObject) {
        return resolveOptionalProfessorName(extractProfessorName(jsonObject));
    }

    public static String getLectureName(JSONObject jsonObject) {
        return resolveDirtyLectureName(extractLectureName(jsonObject));
    }

    public static String getMajorType(JSONObject jsonObject) {
        return resolveDirtyMajorType(extractMajorType(jsonObject));
    }

    public static String extractYear(JSONObject jsonObject) {
        return String.valueOf(jsonObject.get(ESTABLISHED_YEAR));
    }

    public static String extractSemester(JSONObject jsonObject) {
        return String.valueOf(jsonObject.get(ESTABLISHED_SEMESTER).toString().charAt(0));
    }

    public static String extractPlaceSchedule(JSONObject jsonObject) {
        return String.valueOf(jsonObject.get(PLACE_SCHEDULE));
    }

    public static String extractProfessorName(JSONObject jsonObject) {
        return String.valueOf(jsonObject.get(REPRESENT_PROFESSOR_NAME));
    }

    public static String extractLectureFacultyType(JSONObject jsonObject) {
        return String.valueOf(jsonObject.get(FACULTY_DIVISION));
    }

    public static String extractLectureCode(JSONObject jsonObject) {
        return String.valueOf(jsonObject.get(SUBJECT_CODE));
    }

    public static String extractLectureName(JSONObject jsonObject) {
        return String.valueOf(jsonObject.get(SUBJECT_NAME));
    }

    public static String extractEvaluationType(JSONObject jsonObject) {
        return String.valueOf(jsonObject.get(EVALUATION_TYPE));
    }

    public static String extractDivideClassNumber(JSONObject jsonObject) {
        return String.valueOf(jsonObject.get(DIVIDE_CLASS_NUMBER));
    }

    public static String extractMajorType(JSONObject jsonObject) {
        return String.valueOf(jsonObject.get(DEPARTMENT));
    }

    public static Double extractLecturePoint(JSONObject jsonObject) {
        return Double.parseDouble(String.valueOf(jsonObject.get(POINT)));
    }

    public static String extractCapacityType(JSONObject jsonObject) {
        return String.valueOf(jsonObject.get(CAPACITY_TYPE));
    }

    public static Integer extractTargetGrade(JSONObject jsonObject) {
        return Integer.parseInt(String.valueOf(jsonObject.get(TARGET_GRADE)));
    }

    private static String resolveSemester(String year, String semester) {
        return year + "-" + semester;
    }

    private static String resolveOptionalProfessorName(String professorName) {
        return professorName.isEmpty() ? "-" : professorName;   // TODO refactor: null
    }

    private static String resolveDirtyMajorType(String majorType) {
        if (majorType.contains("·")) {
            return majorType.replace("·", "-");
        }
        return majorType;
    }

    private static String resolveDirtyLectureName(String lectureName) {
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

    private static String removeWordAndSurrounds(String target, String word) {
        // 더 까다로운 순서대로 정렬되어있어야 합니다.
        String[] patternsToRemove = {"(" + word + ")", word + "-", word + "_", word};
        return replaceByPatterns(target, patternsToRemove);
    }

    private static String replaceByPatterns(String target, String[] patternsToRemove) {
        for (String pattern : patternsToRemove) {
            if (target.contains(pattern)) {
                return target.replace(pattern, "");
            }
        }
        return target;
    }
}
