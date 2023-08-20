package usw.suwiki.domain.lecture.domain.repository;

import org.springframework.data.jpa.repository.Query;
import usw.suwiki.domain.lecture.controller.dto.LectureFindOption;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.domain.repository.dao.LecturesAndCountDao;

import java.util.List;


public interface LectureQueryRepository {
    //
    Lecture verifyJsonLecture(String lectureName, String ProfessorName, String majorType);
    //
    List<String> findAllMajorType();
    //
    LecturesAndCountDao findLectureByFindOption(String searchValue, LectureFindOption lectureFindOption);
    LecturesAndCountDao findLectureByMajorType(String searchValue, LectureFindOption lectureFindOption);
    LecturesAndCountDao findAllLectureByFindOption(LectureFindOption lectureFindOption);
    LecturesAndCountDao findAllLectureByMajorType(LectureFindOption lectureFindOption);
}
