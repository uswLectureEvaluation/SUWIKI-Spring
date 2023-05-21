package usw.suwiki.domain.lecture.domain.repository;

import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.controller.dto.LectureFindOption;
import usw.suwiki.domain.lecture.domain.repository.dao.LecturesAndCountDao;

import java.util.List;


public interface LectureRepository {
    void save(Lecture lecture);

    Lecture verifyJsonLecture(String lectureName, String ProfessorName, String majorType);

    Lecture findById(Long id);

    Lecture findByIdPessimisticLock(Long id);

    LecturesAndCountDao findLectureByFindOption(String searchValue, LectureFindOption lectureFindOption);

    LecturesAndCountDao findLectureByMajorType(String searchValue, LectureFindOption lectureFindOption);

    LecturesAndCountDao findAllLectureByFindOption(LectureFindOption lectureFindOption);

    LecturesAndCountDao findAllLectureByMajorType(LectureFindOption lectureFindOption);

    List<String> findAllMajorType();
}
