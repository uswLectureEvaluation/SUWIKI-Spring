package usw.suwiki.domain.lecture;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import usw.suwiki.domain.evaluation.EvaluatePostsToLecture;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class LectureService {

    private final LectureRepository lectureRepository;

    public void cancelLectureValue(EvaluatePostsToLecture dto) {
        Lecture lecture = lectureRepository.findById(dto.getLectureId());
        lecture.cancelLectureValue(dto);
    }

    public void addLectureValue(EvaluatePostsToLecture dto) {
        Lecture lecture = lectureRepository.findById(dto.getLectureId());
        lecture.addLectureValue(dto);
    }

    public void calcLectureAvg(EvaluatePostsToLecture dto) {
        Lecture lecture = lectureRepository.findById(dto.getLectureId());
        lecture.calcLectureAvg();
    }

    public LectureToJsonArray findAllLectureByFindOption(LectureFindOption lectureFindOption) {
        List<LectureResponseDto> dtoList = new ArrayList<>();
        LectureListAndCountDto dto = lectureRepository.findAllLectureByFindOption(lectureFindOption);
        for (Lecture lecture : dto.getLectureList()) {
            dtoList.add(new LectureResponseDto(lecture));
        }

        return new LectureToJsonArray(dtoList, dto.getCount());
    }

    public LectureToJsonArray findAllLectureByMajorType(LectureFindOption lectureFindOption) {
        List<LectureResponseDto> dtoList = new ArrayList<>();
        LectureListAndCountDto dto = lectureRepository.findAllLectureByMajorType(lectureFindOption);
        for (Lecture lecture : dto.getLectureList()) {
            dtoList.add(new LectureResponseDto(lecture));
        }

        return new LectureToJsonArray(dtoList, dto.getCount());
    }

    public LectureToJsonArray findLectureByFindOption(String searchValue, LectureFindOption lectureFindOption) {
        List<LectureResponseDto> dtoList = new ArrayList<>();
        LectureListAndCountDto dto = lectureRepository.findLectureByFindOption(searchValue, lectureFindOption);
        for (Lecture lecture : dto.getLectureList()) {
            dtoList.add(new LectureResponseDto(lecture));
        }

        return new LectureToJsonArray(dtoList, dto.getCount());
    }

    public LectureToJsonArray findLectureByMajorType(String searchValue, LectureFindOption lectureFindOption) {
        List<LectureResponseDto> dtoList = new ArrayList<>();
        LectureListAndCountDto dto = lectureRepository.findLectureByMajorType(searchValue, lectureFindOption);
        for (Lecture lecture : dto.getLectureList()) {
            dtoList.add(new LectureResponseDto(lecture));
        }

        return new LectureToJsonArray(dtoList, dto.getCount());
    }

    public LectureDetailResponseDto findByIdDetail(Long id) {
        Lecture lecture = lectureRepository.findById(id);
        LectureDetailResponseDto dto = new LectureDetailResponseDto(lecture);
        return dto;
    }

    public Lecture findById(Long id) {
        return lectureRepository.findById(id);
    }

    public List<String> findAllMajorType() {
        List<String> resultList = lectureRepository.findAllMajorType();
        return resultList;
    }

}
