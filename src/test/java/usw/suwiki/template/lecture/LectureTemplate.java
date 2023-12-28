package usw.suwiki.template.lecture;

import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.domain.LectureDetail;

public class LectureTemplate {
    public static final String SEMESTER = "2022-2, 2023-2";
    public static final String PROFESSOR = "신호진";
    public static final String NAME = "데이터 구조";
    public static final String MAJOR_TYPE = "컴퓨터 학부";
    public static final String LECTURE_TYPE = "전핵";
    public static final LectureDetail LECTURE_DETAIL = LectureDetailTemplate.createfirstDummyLectureDetail();

    public static Lecture createFirstDummyLecture() {
        return createDummyLecture(SEMESTER, PROFESSOR, NAME, MAJOR_TYPE, LECTURE_TYPE, LECTURE_DETAIL);
    }

    public static Lecture createDummyLecture(
            String semester,
            String professor,
            String name,
            String majorType,
            String type,
            LectureDetail lectureDetail
    ) {
        return Lecture.builder()
                .semester(semester)
                .professor(professor)
                .name(name)
                .majorType(majorType)
                .type(type)
                .lectureDetail(lectureDetail)
                .build();
    }
}
