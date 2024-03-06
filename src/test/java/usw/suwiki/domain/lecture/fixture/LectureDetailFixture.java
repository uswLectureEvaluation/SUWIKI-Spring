package usw.suwiki.domain.lecture.fixture;

public class LectureDetailFixture {
    public static final String CODE = "01760";
    public static final double POINT = 0;
    public static final String CAPPR_TYPE = "A형(강의식 수업)";
    public static final String DICL_NO = "001";
    public static final int GRADE = 2;
    public static final String EVALUATE_TYPE = "상대평가";

    public static LectureDetail createFirstDummy() {
        return createDummy(CODE, POINT, CAPPR_TYPE, DICL_NO, GRADE, EVALUATE_TYPE);
    }


    public static LectureDetail createDummy(
            String code,
            double point,
            String capprType,
            String diclNo,
            int grade,
            String evaluateType
    ) {
        return LectureDetail.builder()
                .code(code)
                .point(point)
                .capprType(capprType)
                .diclNo(diclNo)
                .grade(grade)
                .evaluateType(evaluateType)
                .build();
    }
}
