package usw.suwiki.domain.lecture;

import org.springframework.data.domain.Slice;
import usw.suwiki.domain.lecture.controller.dto.LectureFindOption;
import usw.suwiki.domain.lecture.domain.LectureSchedule;

import java.util.List;
import java.util.Optional;


public interface LectureCustomRepository {

    Slice<Lecture> findCurrentSemesterLectures(
        final Long cursorId,
        final int limit,
        final String keyword,
        final String majorType,
        final Integer grade
    );

    List<LectureSchedule> findAllLectureSchedulesByLectureSemesterContains(String semester);

    Optional<Lecture> findByExtraUniqueKey(
        String lectureName,
        String ProfessorName,
        String majorType,
        String dividedClassNumber
    );

    List<String> findAllMajorType();

    LecturesAndCountDao findLectureByFindOption(
        String searchValue,
        LectureFindOption lectureFindOption
    );

    LecturesAndCountDao findLectureByMajorType(
        String searchValue,
        LectureFindOption lectureFindOption
    );

    LecturesAndCountDao findAllLectureByFindOption(LectureFindOption lectureFindOption);

    LecturesAndCountDao findAllLectureByMajorType(LectureFindOption lectureFindOption);
}
