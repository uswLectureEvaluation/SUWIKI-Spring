package usw.suwiki.service.lecture;

import usw.suwiki.dto.evaluate.EvaluatePostsToLecture;
import usw.suwiki.dto.lecture.LectureDetailResponseDto;
import usw.suwiki.dto.lecture.LectureFindOption;
import usw.suwiki.dto.lecture.LectureResponseDto;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.repository.lecture.JpaLectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class LectureService {

    private final JpaLectureRepository lectureRepository;

    public void cancelLectureValue(EvaluatePostsToLecture dto){
        Lecture lecture = lectureRepository.findById(dto.getLectureId());
        lecture.cancelLectureValue(dto);
    }

    public void addLectureValue(EvaluatePostsToLecture dto){
        Lecture lecture = lectureRepository.findById(dto.getLectureId());
        lecture.addLectureValue(dto);
    }

    public void calcLectureAvg(EvaluatePostsToLecture dto){
        Lecture lecture = lectureRepository.findById(dto.getLectureId());
        lecture.calcLectureAvg();
    }

    public List<LectureResponseDto> findAllLectureByFindOption(LectureFindOption lectureFindOption){
        List<LectureResponseDto> dtoList = new ArrayList<>();
        List<Lecture> lectureList = lectureRepository.findAllLectureByFindOption(lectureFindOption);
        for (Lecture lecture : lectureList) {
            dtoList.add(new LectureResponseDto(lecture));
        }

        return dtoList;
    }

    public List<LectureResponseDto> findLectureByFindOption(String searchValue ,LectureFindOption lectureFindOption){
        List<LectureResponseDto> dtoList = new ArrayList<>();
        List<Lecture> lectureList = lectureRepository.findLectureByFindOption(searchValue,lectureFindOption);
        for (Lecture lecture : lectureList) {
            dtoList.add(new LectureResponseDto(lecture));
        }

        return dtoList;
    }

    public LectureDetailResponseDto findByIdDetail(Long id){
        Lecture lecture = lectureRepository.findById(id);
        LectureDetailResponseDto dto = new LectureDetailResponseDto(lecture);
        return dto;
    }

    public Lecture findById(Long id){
        return lectureRepository.findById(id);
    }

}
