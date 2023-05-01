package usw.suwiki.domain.lecture.domain.repository;

import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.controller.dto.LectureFindOption;
import usw.suwiki.domain.lecture.controller.dto.LecturesAndCountDto;

import java.util.List;


public interface LectureRepository {
    void save(Lecture lecture);

    Lecture verifyJsonLecture(String lectureName, String ProfessorName, String majorType);

    Lecture findById(Long id);

    Lecture findByIdPessimisticLock(Long id);

    LecturesAndCountDto findLectureByFindOption(String searchValue, LectureFindOption lectureFindOption);

    LecturesAndCountDto findLectureByMajorType(String searchValue, LectureFindOption lectureFindOption);

    LecturesAndCountDto findAllLectureByFindOption(LectureFindOption lectureFindOption);

    LecturesAndCountDto findAllLectureByMajorType(LectureFindOption lectureFindOption);

    List<String> findAllMajorType();
}
