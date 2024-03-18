package usw.suwiki.domain.lecture.schedule.service;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.LectureException;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.lecture.LectureQueryRepository;
import usw.suwiki.domain.lecture.LectureRepository;
import usw.suwiki.domain.lecture.dto.LectureResponse;
import usw.suwiki.domain.lecture.schedule.LectureSchedule;
import usw.suwiki.domain.lecture.schedule.LectureScheduleQueryRepository;
import usw.suwiki.domain.lecture.schedule.LectureScheduleRepository;
import usw.suwiki.domain.lecture.schedule.data.JsonLecture;
import usw.suwiki.domain.lecture.schedule.data.LectureStringConverter;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LectureScheduleService {
  private final LectureScheduleQueryRepository lectureScheduleQueryRepository;
  private final LectureScheduleRepository lectureScheduleRepository;
  private final LectureQueryRepository lectureQueryRepository;
  private final LectureRepository lectureRepository;

  private final SemesterProvider semesterProvider;

  public LectureResponse.Lectures findPagedLecturesBySchedule(
    Long cursorId,
    int limit,
    String keyword,
    String major,
    Integer grade
  ) {
    Slice<Lecture> lectures = lectureQueryRepository.findCurrentSemesterLectures(cursorId, limit, keyword, major, grade);
    return new LectureResponse.Lectures(lectures.isLast(), toPaginationResponse(lectures));
  }

  // todo: 쿼리 개선하기
  private List<LectureResponse.Lecture> toPaginationResponse(Slice<Lecture> lectures) {
    return lectures.stream().flatMap(lecture -> {
        List<String> placeSchedules = lectureScheduleQueryRepository.findAllPlaceSchedulesByLectureId(lecture.getId());
        return placeSchedules.isEmpty()
          ? Stream.of(LectureScheduleMapper.toEmptyCellResponse(lecture))
          : toResponseWithCells(lecture, placeSchedules);
      })
      .toList();
  }

  private Stream<LectureResponse.Lecture> toResponseWithCells(Lecture lecture, List<String> placeSchedules) {
    return placeSchedules.stream().map(placeSchedule ->
      LectureScheduleMapper.toResponse(lecture, LectureStringConverter.chunkToLectureCells(placeSchedule))
    );
  }

  @Async
  @Transactional(propagation = Propagation.MANDATORY)
  public void bulkApplyJsonLectures(String filePath) {
    List<JsonLecture> jsonLectures = deserializeJsonFromPath(filePath).stream()
      .map(rawObject -> JsonLecture.from((JSONObject) rawObject))
      .toList();

    deleteAllRemovedLectures(jsonLectures);
    deleteAllRemovedLectureSchedules(jsonLectures);
    jsonLectures.forEach(this::insertJsonLectureOrLectureSchedule);
  }

  private JSONArray deserializeJsonFromPath(String filePath) {
    try {
      Reader reader = new FileReader(filePath);
      JSONParser parser = new JSONParser();
      return (JSONArray) parser.parse(reader);
    } catch (IOException | ParseException ex) {
      ex.printStackTrace();
      throw new LectureException(ExceptionType.SERVER_ERROR); // todo: do not throw server error
    }
  }

  private void deleteAllRemovedLectures(List<JsonLecture> jsonLectures) {
    lectureRepository.findAllBySemesterContains(semesterProvider.semester()).stream()
      .filter(it -> jsonLectures.stream().noneMatch(jsonLecture -> jsonLecture.isLectureEqual(it)))
      .forEach(lecture -> {
        if (lecture.isOld()) {
          lecture.removeSemester(semesterProvider.semester());
        } else {
          lectureRepository.delete(lecture);
        }
      });
  }

  private void deleteAllRemovedLectureSchedules(List<JsonLecture> jsonLectures) {
    List<LectureSchedule> schedulesToDelete =
      lectureScheduleQueryRepository.findAllSchedulesBySemesterContains(semesterProvider.semester()).stream() // 기존의 스케줄이 삭제된 케이스 필터링 : O(N^2) 비교
        .filter(it -> jsonLectures.stream().noneMatch(jsonLecture -> jsonLecture.isLectureAndPlaceScheduleEqual(it)))
        .toList();

    lectureScheduleRepository.deleteAllInBatch(schedulesToDelete);
  }

  private void insertJsonLectureOrLectureSchedule(JsonLecture jsonLecture) {
    Optional<Lecture> optionalLecture = lectureQueryRepository.findByExtraUniqueKey(
      jsonLecture.getLectureName(),
      jsonLecture.getProfessor(),
      jsonLecture.getMajorType(),
      jsonLecture.getDividedClassNumber()
    );

    if (optionalLecture.isPresent()) {
      extendSemesterOfLecture(optionalLecture.get(), jsonLecture);
    } else {
      insertNewLecture(jsonLecture);
    }
  }

  private void extendSemesterOfLecture(Lecture lecture, JsonLecture jsonLecture) {
    lecture.addSemester(jsonLecture.getSelectedSemester());
    List<LectureSchedule> schedules = lectureScheduleQueryRepository.findAllByLectureId(lecture.getId());
    if (schedules.stream().noneMatch(jsonLecture::isLectureAndPlaceScheduleEqual)) {
      saveLectureSchedule(lecture.getId(), jsonLecture);
    }
  }

  private void insertNewLecture(JsonLecture jsonLecture) {
    Lecture saved = lectureRepository.save(jsonLecture.toEntity());
    saveLectureSchedule(saved.getId(), jsonLecture);
  }

  private void saveLectureSchedule(Long lectureId, JsonLecture jsonLecture) {
    if (jsonLecture.isValidPlaceSchedule()) {
      LectureSchedule schedule = new LectureSchedule(lectureId, jsonLecture.getPlaceSchedule(), semesterProvider.semester());
      lectureScheduleRepository.save(schedule);
    }
  }

  private List<LectureSchedule> resolveDeletedLectureScheduleList(
    List<JsonLecture> jsonLectures,
    List<LectureSchedule> currentSemeterLectureScheduleList
  ) {
    return currentSemeterLectureScheduleList.stream() // 기존의 스케줄이 삭제된 케이스 필터링 : O(N^2) 비교
      .filter(it -> jsonLectures.stream().noneMatch(vo -> vo.isLectureAndPlaceScheduleEqual(it))) // todo: flatmap
      .toList();
  }
}
