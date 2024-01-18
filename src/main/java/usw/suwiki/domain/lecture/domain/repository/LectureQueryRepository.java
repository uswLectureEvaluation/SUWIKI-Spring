package usw.suwiki.domain.lecture.domain.repository;

import java.util.List;
import java.util.Optional;
import usw.suwiki.domain.lecture.controller.dto.LectureFindOption;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.domain.repository.dao.LecturesAndCountDao;


public interface LectureQueryRepository {

    Optional<Lecture> verifyJsonLecture(String lectureName, String ProfessorName, String majorType);

    List<String> findAllMajorType();

    LecturesAndCountDao findLectureByFindOption(String searchValue, LectureFindOption lectureFindOption);

    LecturesAndCountDao findLectureByMajorType(String searchValue, LectureFindOption lectureFindOption);

    LecturesAndCountDao findAllLectureByFindOption(LectureFindOption lectureFindOption);

    LecturesAndCountDao findAllLectureByMajorType(LectureFindOption lectureFindOption);
}
