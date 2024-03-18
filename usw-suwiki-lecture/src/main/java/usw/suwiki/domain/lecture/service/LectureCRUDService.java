package usw.suwiki.domain.lecture.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.LectureException;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.lecture.LectureRepository;
import usw.suwiki.domain.lecture.LecturesAndCountDao;
import usw.suwiki.domain.lecture.dto.LectureFindOption;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LectureCRUDService {
  private final LectureRepository lectureRepository;

  public List<String> loadMajorTypes() {
    return lectureRepository.findAllMajorType();
  }

  public Lecture loadLectureFromIdPessimisticLock(Long id) {
    return lectureRepository.findForUpdateById(id)
      .orElseThrow(() -> new LectureException(ExceptionType.LECTURE_NOT_FOUND));
  }

  public LecturesAndCountDao loadLectureByKeywordAndOption(String keyword, LectureFindOption option) {
    return lectureRepository.findLectureByFindOption(keyword, option);
  }

  public LecturesAndCountDao loadLectureByKeywordAndMajor(String searchValue, LectureFindOption option) {
    return lectureRepository.findLectureByMajorType(searchValue, option);
  }

  public LecturesAndCountDao loadLecturesByOption(LectureFindOption option) {
    return lectureRepository.findAllLectureByFindOption(option);
  }

  public LecturesAndCountDao loadLecturesByMajor(LectureFindOption option) {
    return lectureRepository.findAllLectureByMajorType(option);
  }
}
