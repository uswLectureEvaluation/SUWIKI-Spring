package usw.suwiki.domain.lecture;

import org.springframework.data.domain.Slice;
import usw.suwiki.domain.lecture.dto.LectureFindOption;

import java.util.List;
import java.util.Optional;

public interface LectureCustomRepository {

    Slice<Lecture> findCurrentSemesterLectures(Long cursorId, int limit, String keyword, String majorType, Integer grade);

    List<LectureSchedule> findAllLectureSchedulesByLectureSemesterContains(String semester);

    Optional<Lecture> findByExtraUniqueKey(String lectureName, String ProfessorName, String majorType, String dividedClassNumber);

    List<String> findAllMajorType();

    LecturesAndCountDao findLectureByFindOption(String searchValue, LectureFindOption lectureFindOption);

    LecturesAndCountDao findLectureByMajorType(String searchValue, LectureFindOption lectureFindOption);

    LecturesAndCountDao findAllLectureByFindOption(LectureFindOption lectureFindOption);

    LecturesAndCountDao findAllLectureByMajorType(LectureFindOption lectureFindOption);
}
