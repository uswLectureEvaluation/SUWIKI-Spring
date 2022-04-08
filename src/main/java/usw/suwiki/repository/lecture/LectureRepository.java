package usw.suwiki.repository.lecture;

import usw.suwiki.domain.evaluation.EvaluatePosts;
import usw.suwiki.dto.lecture.LectureFindOption;
import usw.suwiki.domain.lecture.Lecture;

import java.util.List;

public interface LectureRepository {
    void save(Lecture lecture);
    Lecture findOneBySubAndProf(String lectureNameName,String ProfessorName);
    Lecture findById(Long id);
    List<Lecture> findLectureByFindOption(String searchValue, LectureFindOption lectureFindOption);
    List<Lecture> findAllLectureByFindOption(LectureFindOption lectureFindOption);
}
