package usw.suwiki.domain.lecture.domain.repository;

import java.util.List;
import org.springframework.data.domain.Slice;
import usw.suwiki.domain.lecture.controller.dto.LectureFindOption;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.domain.repository.dao.LecturesAndCountDao;


public interface LectureCustomRepository {
    Slice<Lecture> findCurrentSemesterLecturesPage(
            final Long cursorId,
            final int limit,
            final String keyword,
            final String majorType,
            final Integer grade
    );

    Lecture verifyJsonLecture(String lectureName, String ProfessorName, String majorType);

    List<String> findAllMajorType();

    LecturesAndCountDao findLectureByFindOption(String searchValue, LectureFindOption lectureFindOption);

    LecturesAndCountDao findLectureByMajorType(String searchValue, LectureFindOption lectureFindOption);

    LecturesAndCountDao findAllLectureByFindOption(LectureFindOption lectureFindOption);

    LecturesAndCountDao findAllLectureByMajorType(LectureFindOption lectureFindOption);
}
