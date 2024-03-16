package usw.suwiki.domain.lecture.service;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.common.response.NoOffsetPaginationResponse;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.LectureException;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.lecture.LectureRepository;
import usw.suwiki.domain.lecture.LecturesAndCountDao;
import usw.suwiki.domain.lecture.data.EvaluatedData;
import usw.suwiki.domain.lecture.data.JSONLectureVO;
import usw.suwiki.domain.lecture.dto.LectureAndCountResponseForm;
import usw.suwiki.domain.lecture.dto.LectureDetailResponseDto;
import usw.suwiki.domain.lecture.dto.LectureFindOption;
import usw.suwiki.domain.lecture.dto.LectureResponseDto;
import usw.suwiki.domain.lecture.dto.LectureWithOptionalScheduleResponse;
import usw.suwiki.domain.lecture.schedule.LectureSchedule;
import usw.suwiki.domain.lecture.schedule.LectureScheduleRepository;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LectureService {
  private final LectureCRUDService lectureCRUDService;
  private final LectureRepository lectureRepository;
  private final LectureScheduleRepository lectureScheduleRepository;

  @Value("${business.current-semester}") // todo: 싱글톤 객체 (bean)은 상태를 가지면 안된다.
  private String currentSemester;

  @Transactional
  public void evaluate(Long lectureId, EvaluatedData evaluatedData) {
    Lecture lecture = lectureCRUDService.loadLectureFromIdPessimisticLock(lectureId);
    lecture.evaluated(evaluatedData);
  }

  @Transactional
  public void updateEvaluation(Long lectureId, EvaluatedData current, EvaluatedData update) {
    Lecture lecture = lectureCRUDService.loadLectureFromIdPessimisticLock(lectureId);
    lecture.updateEvaluation(current, update);
  }

  public LectureAndCountResponseForm readLectureByKeyword(String keyword, LectureFindOption option) {
    return option.passMajorFiltering() ? readLectureByKeywordAndOption(keyword, option) : readLectureByKeywordAndMajor(keyword, option);
  }

  public LectureAndCountResponseForm readAllLecture(LectureFindOption option) {
    if (option.passMajorFiltering()) {
      return readAllLectureByOption(option);
    }
    return readAllLectureByMajorType(option);
  }

  public LectureDetailResponseDto readLectureDetail(Long id) {
    Lecture lecture = findLectureById(id);
    return new LectureDetailResponseDto(lecture);
  }

  public NoOffsetPaginationResponse<LectureWithOptionalScheduleResponse> findPagedLecturesWithSchedule(
    Long cursorId,
    int limit,
    String keyword,
    String major,
    Integer grade
  ) {
    Slice<Lecture> lectureSlice = lectureRepository
      .findCurrentSemesterLectures(cursorId, limit, keyword, major, grade);

    List<LectureWithOptionalScheduleResponse> result = buildLectureWithOptionalScheduleResponseList(lectureSlice);
    return NoOffsetPaginationResponse.of(result, lectureSlice.isLast());
  }

  private static List<LectureWithOptionalScheduleResponse> buildLectureWithOptionalScheduleResponseList(
    Slice<Lecture> slice
  ) {
    List<LectureWithOptionalScheduleResponse> result = new ArrayList<>();

    for (Lecture lecture : slice) {
      if (lecture.getScheduleList().isEmpty()) {
        result.add(LectureWithOptionalScheduleResponse.from(lecture));
      } else {
        result.addAll(lecture.getScheduleList()
          .stream()
          .map(LectureWithOptionalScheduleResponse::from)
          .toList());
      }
    }
    return result;
  }

  public Lecture findLectureById(Long id) {
    return lectureRepository.findById(id)
      .orElseThrow(() -> new LectureException(ExceptionType.LECTURE_NOT_FOUND));
  }

  @Transactional
  public void bulkApplyLectureJsonFile(String filePath) {
    JSONArray jsonArray = resolveJsonArrayFromJsonFile(filePath);
    List<JSONLectureVO> jsonLectureVOList = convertJSONArrayToVOList(jsonArray);
    bulkApplyJsonLectureList(jsonLectureVOList);
  }

  private static JSONArray resolveJsonArrayFromJsonFile(String filePath) {
    try {
      Reader reader = new FileReader(filePath);
      JSONParser parser = new JSONParser();
      Object obj = parser.parse(reader);

      return (JSONArray) obj;
    } catch (IOException | ParseException ex) {
      ex.printStackTrace();
      throw new LectureException(ExceptionType.SERVER_ERROR);
    }
  }

  private static List<JSONLectureVO> convertJSONArrayToVOList(JSONArray jsonArray) {
    List<JSONLectureVO> jsonLectureVOList = new ArrayList<>();
    for (Object rawObject : jsonArray) {
      JSONLectureVO jsonLectureVO = JSONLectureVO.from((JSONObject) rawObject);
      jsonLectureVOList.add(jsonLectureVO);
    }
    return jsonLectureVOList;
  }


  @Transactional(propagation = Propagation.MANDATORY)
  public void bulkApplyJsonLectureList(List<JSONLectureVO> jsonLectureVOList) {
    deleteAllRemovedLectures(jsonLectureVOList);
    deleteAllRemovedLectureSchedules(jsonLectureVOList);

    saveAllLecturesOrLectureSchedules(jsonLectureVOList);
  }

  private void deleteAllRemovedLectures(List<JSONLectureVO> jsonLectureVOList) {
    List<Lecture> currentSemeterLectureList = lectureRepository.findAllBySemesterContains(currentSemester);
    List<Lecture> removedLectureList =
      currentSemeterLectureList.stream()
        .filter(it -> jsonLectureVOList.stream().noneMatch(vo -> vo.isLectureEqual(it)))
        .toList();

    for (Lecture lecture : removedLectureList) {
      if (lecture.isOld()) {
        lecture.removeSemester(currentSemester);
      } else {
        lectureRepository.delete(lecture);
      }
    }
  }


  private void deleteAllRemovedLectureSchedules(List<JSONLectureVO> jsonLectureVOList) {
    List<LectureSchedule> currentSemeterLectureScheduleList = lectureRepository
      .findAllLectureSchedulesByLectureSemesterContains(currentSemester);

    List<LectureSchedule> removedLectureScheduleList = // 기존의 스케줄이 삭제된 케이스 필터링 : O(N^2) 비교
      currentSemeterLectureScheduleList.stream()
        .filter(it -> jsonLectureVOList.stream().noneMatch(vo -> vo.isLectureAndPlaceScheduleEqual(it)))
        .toList();

    lectureScheduleRepository.deleteAll(removedLectureScheduleList);
  }

  private void saveAllLecturesOrLectureSchedules(List<JSONLectureVO> jsonLectureVOList) {
    jsonLectureVOList.forEach(this::insertJsonLectureOrLectureSchedule);
  }

  private void insertJsonLectureOrLectureSchedule(
    JSONLectureVO jsonLectureVO
  ) {
    Optional<Lecture> optionalLecture = lectureRepository.findByExtraUniqueKey(
      jsonLectureVO.getLectureName(),
      jsonLectureVO.getProfessor(),
      jsonLectureVO.getMajorType(),
      jsonLectureVO.getDividedClassNumber()
    );

    if (optionalLecture.isPresent()) {
      Lecture lecture = optionalLecture.get();
      lecture.addSemester(jsonLectureVO.getSelectedSemester());

      boolean isThereNewSchedule = lecture.getScheduleList().stream()
        .noneMatch(jsonLectureVO::isLectureAndPlaceScheduleEqual);
      if (isThereNewSchedule) {
        saveLectureSchedule(jsonLectureVO, lecture);
      }

    } else {
      Lecture newLecture = jsonLectureVO.toEntity();
      saveLectureSchedule(jsonLectureVO, newLecture);
      lectureRepository.save(newLecture);
    }
  }

  private void saveLectureSchedule(JSONLectureVO jsonLectureVO, Lecture lecture) {
    if (jsonLectureVO.isPlaceScheduleValid()) {
      LectureSchedule schedule = LectureSchedule.builder()
        .lecture(lecture)
        .placeSchedule(jsonLectureVO.getPlaceSchedule())
        .semester(currentSemester)
        .build();
      lectureScheduleRepository.save(schedule);
    }
  }

  private static List<LectureSchedule> resolveDeletedLectureScheduleList(
    List<JSONLectureVO> jsonLectureVOList,
    List<LectureSchedule> currentSemeterLectureScheduleList
  ) {
    return currentSemeterLectureScheduleList.stream() // 기존의 스케줄이 삭제된 케이스 필터링 : O(N^2) 비교
      .filter(it -> jsonLectureVOList.stream().noneMatch(vo -> vo.isLectureAndPlaceScheduleEqual(it))) // todo: flatmap
      .toList();
  }

  private LectureAndCountResponseForm readLectureByKeywordAndOption(String keyword, LectureFindOption option) {
    LecturesAndCountDao lectureInfo = lectureCRUDService.loadLectureByKeywordAndOption(keyword, option);
    return createLectureResponseForm(lectureInfo);
  }

  private LectureAndCountResponseForm readLectureByKeywordAndMajor(String searchValue, LectureFindOption option) {
    LecturesAndCountDao lectureInfo = lectureCRUDService.loadLectureByKeywordAndMajor(searchValue, option);
    return createLectureResponseForm(lectureInfo);
  }

  private LectureAndCountResponseForm readAllLectureByOption(LectureFindOption option) {
    LecturesAndCountDao lectureInfo = lectureCRUDService.loadLecturesByOption(option);
    return createLectureResponseForm(lectureInfo);
  }

  private LectureAndCountResponseForm readAllLectureByMajorType(LectureFindOption option) {
    LecturesAndCountDao lectureInfo = lectureCRUDService.loadLecturesByMajor(option);
    return createLectureResponseForm(lectureInfo);
  }

  private LectureAndCountResponseForm createLectureResponseForm(LecturesAndCountDao lectureInfo) {
    List<LectureResponseDto> dtoList = new ArrayList<>();
    for (Lecture lecture : lectureInfo.getLectureList()) {
      dtoList.add(new LectureResponseDto(lecture));
    }
    return new LectureAndCountResponseForm(dtoList, lectureInfo.getCount());
  }
}