package usw.suwiki.domain.lecture.schedule.service;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.LectureException;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.lecture.schedule.LectureSchedule;
import usw.suwiki.domain.lecture.schedule.LectureScheduleRepository;
import usw.suwiki.domain.lecture.schedule.data.JsonLecture;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LectureScheduleService {
  private final LectureScheduleRepository lectureScheduleRepository;
  private final SemesterProvider semesterProvider;

  public void bulkApplyLectureJsonFile(String filePath) {
    List<JsonLecture> jsonLectures = deserializeJsonFile(filePath).stream()
      .map(rawObject -> JsonLecture.from((JSONObject) rawObject))
      .toList();

    bulkApplyJsonLectureList(jsonLectures);
  }

  private JSONArray deserializeJsonFile(String filePath) {
    try {
      Reader reader = new FileReader(filePath);
      JSONParser parser = new JSONParser();
      return (JSONArray) parser.parse(reader);
    } catch (IOException | ParseException ex) {
      ex.printStackTrace();
      throw new LectureException(ExceptionType.SERVER_ERROR); // todo: do not throw server error
    }
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public void bulkApplyJsonLectureList(List<JsonLecture> jsonLectures) {
    deleteAllRemovedLectures(jsonLectures);
    deleteAllRemovedLectureSchedules(jsonLectures);
    jsonLectures.forEach(this::insertJsonLectureOrLectureSchedule);
  }

  private void deleteAllRemovedLectures(List<JsonLecture> jsonLectures) {
    List<Lecture> removedLectureList = lectureRepository.findAllBySemesterContains(currentSemester).stream()
      .filter(it -> jsonLectures.stream().noneMatch(vo -> vo.isLectureEqual(it)))
      .toList();

    for (Lecture lecture : removedLectureList) {
      if (lecture.isOld()) {
        lecture.removeSemester(currentSemester);
      } else {
        lectureRepository.delete(lecture);
      }
    }
  }

  private void deleteAllRemovedLectureSchedules(List<JsonLecture> jsonLectures) {
    List<LectureSchedule> currentSemeterLectureScheduleList = lectureRepository
      .findAllLectureSchedulesByLectureSemesterContains(currentSemester);

    List<LectureSchedule> removedLectureScheduleList = currentSemeterLectureScheduleList.stream() // 기존의 스케줄이 삭제된 케이스 필터링 : O(N^2) 비교
      .filter(it -> jsonLectures.stream().noneMatch(vo -> vo.isLectureAndPlaceScheduleEqual(it)))
      .toList();

    lectureScheduleRepository.deleteAll(removedLectureScheduleList);
  }

  private void insertJsonLectureOrLectureSchedule(JsonLecture jsonLecture) {
    Optional<Lecture> optionalLecture = lectureRepository.findByExtraUniqueKey(
      jsonLecture.getLectureName(),
      jsonLecture.getProfessor(),
      jsonLecture.getMajorType(),
      jsonLecture.getDividedClassNumber()
    );

    if (optionalLecture.isPresent()) {
      Lecture lecture = optionalLecture.get();
      lecture.addSemester(jsonLecture.getSelectedSemester());

      if (lecture.getScheduleList().stream().noneMatch(jsonLecture::isLectureAndPlaceScheduleEqual)) {
        saveLectureSchedule(jsonLecture, lecture);
      }

    } else {
      Lecture newLecture = jsonLecture.toEntity();
      saveLectureSchedule(jsonLecture, newLecture);
      lectureRepository.save(newLecture);
    }
  }

  private void saveLectureSchedule(JsonLecture jsonLecture, Lecture lecture) {
    if (jsonLecture.isPlaceScheduleValid()) {
      LectureSchedule schedule = LectureSchedule.builder()
        .lecture(lecture)
        .placeSchedule(jsonLecture.getPlaceSchedule())
        .semester(semesterProvider.getCurrentSemester())
        .build();
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
