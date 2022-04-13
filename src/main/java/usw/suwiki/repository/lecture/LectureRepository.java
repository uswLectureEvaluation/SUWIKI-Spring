package usw.suwiki.repository.lecture;

import usw.suwiki.dto.lecture.LectureFindOption;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.dto.lecture.LectureListAndCountDto;

import java.util.List;

public interface LectureRepository {
    void save(Lecture lecture);
    Lecture findOneBySubAndProf(String lectureNameName,String ProfessorName);
    Lecture findById(Long id);
    LectureListAndCountDto findLectureByFindOption(String searchValue, LectureFindOption lectureFindOption);
    LectureListAndCountDto findAllLectureByFindOption(LectureFindOption lectureFindOption);
    List<String> findAllMajorType();
}
