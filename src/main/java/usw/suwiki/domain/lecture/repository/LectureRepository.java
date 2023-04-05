package usw.suwiki.domain.lecture.repository;

import usw.suwiki.domain.lecture.entity.Lecture;
import usw.suwiki.domain.lecture.LectureFindOption;
import usw.suwiki.domain.lecture.dto.LectureListAndCountDto;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

public interface LectureRepository {
    void save(Lecture lecture);

    Lecture verifyJsonLecture(String lectureName, String ProfessorName, String majorType);

    Lecture findById(Long id);

    Lecture findByIdPessimisticLock(Long id);

    LectureListAndCountDto findLectureByFindOption(String searchValue, LectureFindOption lectureFindOption);

    LectureListAndCountDto findLectureByMajorType(String searchValue, LectureFindOption lectureFindOption);

    LectureListAndCountDto findAllLectureByFindOption(LectureFindOption lectureFindOption);

    LectureListAndCountDto findAllLectureByMajorType(LectureFindOption lectureFindOption);

    List<String> findAllMajorType();

}
