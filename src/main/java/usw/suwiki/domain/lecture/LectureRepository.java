package usw.suwiki.domain.lecture;

import java.util.List;

public interface LectureRepository {
    void save(Lecture lecture);

    Lecture verifyJsonLecture(String lectureName, String ProfessorName, String majorType);

    Lecture findById(Long id);

    LectureListAndCountDto findLectureByFindOption(String searchValue, LectureFindOption lectureFindOption);

    LectureListAndCountDto findLectureByMajorType(String searchValue, LectureFindOption lectureFindOption);

    LectureListAndCountDto findAllLectureByFindOption(LectureFindOption lectureFindOption);

    LectureListAndCountDto findAllLectureByMajorType(LectureFindOption lectureFindOption);

    List<String> findAllMajorType();

}
