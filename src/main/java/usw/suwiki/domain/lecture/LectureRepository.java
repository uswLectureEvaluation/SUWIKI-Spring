package usw.suwiki.domain.lecture;

import usw.suwiki.domain.lecture.LectureFindOption;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.lecture.LectureListAndCountDto;

import java.util.List;

public interface LectureRepository {
    void save(Lecture lecture);
    Lecture verifyJsonLecture(String lectureName,String ProfessorName,String majorType);
    Lecture findById(Long id);
    LectureListAndCountDto findLectureByFindOption(String searchValue, LectureFindOption lectureFindOption);
    LectureListAndCountDto findLectureByMajorType(String searchValue, LectureFindOption lectureFindOption);
    LectureListAndCountDto findAllLectureByFindOption(LectureFindOption lectureFindOption);
    LectureListAndCountDto findAllLectureByMajorType(LectureFindOption lectureFindOption);
    List<String> findAllMajorType();

}
