package usw.suwiki.template.lecture;

import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.domain.LectureDetail;

public class LectureTemplate {
    public static final String SEMESTER = "2022-1, 2023-1";
    public static final String PROFESSOR = "신호진";
    public static final String NAME = "데이터 구조";
    public static final String MAJOR_TYPE = "컴퓨터 학부";
    public static final String LECTURE_TYPE = "전핵";
    public static final LectureDetail LECTURE_DETAIL = LectureDetailTemplate.createFirstDummy();

    public static Lecture createFirstDummyLecture() {
        return createDummyLecture(SEMESTER, PROFESSOR, NAME, MAJOR_TYPE, LECTURE_TYPE, LECTURE_DETAIL);
    }

    public static Lecture createDummyLecture(
            String semester,
            String name,
            String type,
            String majorType,
            String professor,
            LectureDetail lectureDetail
    ) {
        return Lecture.builder()
                .semester(semester)
                .name(name)
                .type(type)
                .majorType(majorType)
                .professor(professor)
                .lectureDetail(lectureDetail)
                .build();
    }
}
