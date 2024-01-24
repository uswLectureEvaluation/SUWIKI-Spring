package usw.suwiki.domain.lecture.service;

import static usw.suwiki.global.exception.ExceptionType.LECTURE_NOT_FOUND;

import java.util.ArrayList;
import java.util.List;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.lecture.controller.dto.LectureAndCountResponseForm;
import usw.suwiki.domain.lecture.controller.dto.LectureDetailResponseDto;
import usw.suwiki.domain.lecture.controller.dto.LectureFindOption;
import usw.suwiki.domain.lecture.controller.dto.LectureResponseDto;
import usw.suwiki.domain.lecture.controller.dto.LectureWithScheduleResponse;
import usw.suwiki.domain.lecture.controller.dto.OriginalLectureCellResponse;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.domain.repository.LectureRepository;
import usw.suwiki.domain.lecture.domain.repository.dao.LecturesAndCountDao;
import usw.suwiki.domain.lecture.util.LectureStringConverter;
import usw.suwiki.domain.timetable.entity.TimetableCellSchedule;
import usw.suwiki.global.dto.NoOffsetPaginationResponse;
import usw.suwiki.global.exception.errortype.LectureException;
import usw.suwiki.domain.lecture.domain.repository.LectureRepository;
import usw.suwiki.domain.lecture.domain.repository.dao.LecturesAndCountDao;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.LectureException;
import usw.suwiki.global.util.loadjson.JSONLectureVO;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LectureService {

    private final LectureCRUDService lectureCRUDService;
    private final LectureRepository lectureRepository;

    public LectureAndCountResponseForm readLectureByKeyword(String keyword, LectureFindOption option) {
        if (option.passMajorFiltering()) {
            return readLectureByKeywordAndOption(keyword, option);
        }
        return readLectureByKeywordAndMajor(keyword, option);
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

    public NoOffsetPaginationResponse<LectureWithScheduleResponse> findPagedLecturesWithSchedule(
            Long cursorId,
            int limit,
            String keyword,
            String major,
            Integer grade
    ) {
        Slice<Lecture> lectureSlice = lectureRepository
                .findCurrentSemesterLectures(cursorId, limit, keyword, major, grade);

        Slice<LectureWithScheduleResponse> result = lectureSlice
                .map(this::convertLectureWithSchedule);

        return NoOffsetPaginationResponse.of(result);
    }

    private LectureWithScheduleResponse convertLectureWithSchedule(Lecture lecture) {
        LectureWithScheduleResponse response = LectureWithScheduleResponse.of(lecture);
        String placeSchedule = lecture.getLectureDetail().getPlaceSchedule();

        List<TimetableCellSchedule> scheduleList = LectureStringConverter
                .convertScheduleChunkIntoTimetableCellScheduleList(placeSchedule);

        scheduleList.forEach(it -> response.addOriginalCellResponse(OriginalLectureCellResponse.of(it)));
        return response;
    }


    /**
     * 공통 메서드
     */
    public Lecture findLectureById(Long id) {
        return lectureRepository.findById(id)
                .orElseThrow(() -> new LectureException(LECTURE_NOT_FOUND));
    }


    @Transactional
    public void bulkSaveJsonLectures(String filePath) {
        JSONArray jsonArray = resolveJsonArrayFromJsonFile(filePath);
        bulkSaveLectures(jsonArray);
    }

    private void bulkSaveLectures(JSONArray jsonArray) {
        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) o;
            JSONLectureVO jsonLectureVO = new JSONLectureVO(jsonObject);

            Optional<Lecture> optionalLecture = lectureRepository.findByExtraUniqueKey(
                    jsonLectureVO.getLectureName(),
                    jsonLectureVO.getProfessor(),
                    jsonLectureVO.getMajorType()
            );

            if (optionalLecture.isPresent()) {
                Lecture lecture = optionalLecture.get();
                lecture.addSemester(jsonLectureVO.getSelectedSemester());

                lectureRepository.save(lecture);
            } else {
                Lecture newLecture = jsonLectureVO.toEntity();
                lectureRepository.save(newLecture);
            }
        }
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
