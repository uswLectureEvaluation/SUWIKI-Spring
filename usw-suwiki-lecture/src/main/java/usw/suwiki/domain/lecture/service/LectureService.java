package usw.suwiki.domain.lecture.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.common.response.NoOffsetPaginationResponse;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.LectureException;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.lecture.LectureRepository;
import usw.suwiki.domain.lecture.dto.LectureResponse;
import usw.suwiki.domain.lecture.dto.LectureSearchOption;
import usw.suwiki.domain.lecture.dto.LectureWithOptionalScheduleResponse;
import usw.suwiki.domain.lecture.dto.Lectures;
import usw.suwiki.domain.lecture.model.Evaluation;

import java.util.List;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LectureService {
  private final LectureRepository lectureRepository;

  public List<String> loadMajorTypes() {
    return lectureRepository.findAllMajorTypes();
  }

  public Lecture findLectureById(Long id) {
    return lectureRepository.findById(id)
      .orElseThrow(() -> new LectureException(ExceptionType.LECTURE_NOT_FOUND));
  }

  public LectureResponse.Detail loadLectureDetail(Long lectureId) {
    Lecture lecture = findLectureById(lectureId);
    return LectureMapper.toDetail(lecture);
  }

  public LectureResponse.Simples loadAllLecturesByKeyword(String keyword, LectureSearchOption option) {
    return toResponse(option.passMajorFiltering()
      ? lectureRepository.findAllLecturesByOption(keyword, option)
      : lectureRepository.findAllLecturesByMajorType(keyword, option)
    );
  }

  public LectureResponse.Simples loadAllLectures(LectureSearchOption option) {
    return toResponse(option.passMajorFiltering()
      ? lectureRepository.findAllLecturesByOption(option)
      : lectureRepository.findAllLecturesByMajorType(option)
    );
  }

  private LectureResponse.Simples toResponse(Lectures lectures) {
    return new LectureResponse.Simples(
      lectures.count(),
      lectures.content().stream()
        .map(LectureMapper::toSimple)
        .toList()
    );
  }

  @Transactional
  public void evaluate(Long lectureId, Evaluation evaluation) {
    Lecture lecture = loadLectureFromIdPessimisticLock(lectureId);
    lecture.evaluate(evaluation);
  }

  @Transactional
  public void updateEvaluation(Long lectureId, Evaluation current, Evaluation update) {
    Lecture lecture = loadLectureFromIdPessimisticLock(lectureId);
    lecture.updateEvaluation(current, update);
  }

  private Lecture loadLectureFromIdPessimisticLock(Long id) {
    return lectureRepository.findForUpdateById(id)
      .orElseThrow(() -> new LectureException(ExceptionType.LECTURE_NOT_FOUND));
  }

  public NoOffsetPaginationResponse<LectureWithOptionalScheduleResponse> findPagedLecturesWithSchedule(
    Long cursorId,
    int limit,
    String keyword,
    String major,
    Integer grade
  ) {
    Slice<Lecture> lectureSlice = lectureRepository.findCurrentSemesterLectures(cursorId, limit, keyword, major, grade);
    return NoOffsetPaginationResponse.of(toPaginationResponse(lectureSlice), lectureSlice.isLast());
  }

  private List<LectureWithOptionalScheduleResponse> toPaginationResponse(Slice<Lecture> slice) {
    return slice.stream()
      .flatMap(lecture -> lecture.getScheduleList().isEmpty()
        ? Stream.of(LectureWithOptionalScheduleResponse.from(lecture))
        : lecture.getScheduleList().stream().map(LectureWithOptionalScheduleResponse::from))
      .toList();
//    for (Lecture lecture : slice) {
//      if (lecture.getScheduleList().isEmpty()) {
//        result.add(LectureWithOptionalScheduleResponse.from(lecture));
//      } else {
//        result.addAll(lecture.getScheduleList()
//          .stream()
//          .map(LectureWithOptionalScheduleResponse::from)
//          .toList());
//      }
//    }
//    return result;
  }
}
