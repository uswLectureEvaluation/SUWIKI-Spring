package usw.suwiki.domain.lecture.service;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.domain.repository.LectureRepository;
import usw.suwiki.domain.lecture.domain.repository.dao.LecturesAndCountDao;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.LectureException;
import usw.suwiki.global.util.loadjson.JSONLectureVO;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureCRUDService lectureCRUDService;
    private final LectureRepository lectureRepository;

    @Transactional(readOnly = true)
    public LectureAndCountResponseForm readLectureByKeyword(String keyword, LectureFindOption option) {
        if (option.passMajorFiltering()) {
            return readLectureByKeywordAndOption(keyword, option);
        }
        return readLectureByKeywordAndMajor(keyword, option);
    }

    @Transactional(readOnly = true)
    public LectureAndCountResponseForm readAllLecture(LectureFindOption option) {
        if (option.passMajorFiltering()) {
            return readAllLectureByOption(option);
        }
        return readAllLectureByMajorType(option);
    }

    @Transactional(readOnly = true)
    public LectureDetailResponseDto readLectureDetail(Long id) {
        Lecture lecture = lectureCRUDService.loadLectureFromId(id);
        return new LectureDetailResponseDto(lecture);
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

                lecture.fixOmission(jsonLectureVO);
                lecture.addSemester(jsonLectureVO);

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
