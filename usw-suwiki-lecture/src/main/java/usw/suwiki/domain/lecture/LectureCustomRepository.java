package usw.suwiki.domain.lecture;

import org.springframework.data.domain.Slice;
import usw.suwiki.domain.lecture.dto.LectureSearchOption;
import usw.suwiki.domain.lecture.dto.Lectures;

import java.util.List;
import java.util.Optional;

public interface LectureCustomRepository {

  Slice<Lecture> findCurrentSemesterLectures(Long cursorId, int limit, String keyword, String majorType, Integer grade);

  Optional<Lecture> findByExtraUniqueKey(String lectureName, String professor, String majorType, String dividedClassNumber);

  List<String> findAllMajorTypes();

  Lectures findAllLecturesByOption(String searchValue, LectureSearchOption lectureSearchOption);

  Lectures findAllLecturesByMajorType(String searchValue, LectureSearchOption lectureSearchOption);

  Lectures findAllLecturesByOption(LectureSearchOption lectureSearchOption);

  Lectures findAllLecturesByMajorType(LectureSearchOption lectureSearchOption);
}
