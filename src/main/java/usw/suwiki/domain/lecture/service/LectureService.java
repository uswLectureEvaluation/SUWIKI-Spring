package usw.suwiki.domain.lecture.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import usw.suwiki.domain.evaluation.EvaluatePostsToLecture;
import usw.suwiki.domain.lecture.LectureFindOption;
import usw.suwiki.domain.lecture.LectureToJsonArray;
import usw.suwiki.domain.lecture.dto.LectureDetailResponseDto;
import usw.suwiki.domain.lecture.dto.LectureListAndCountDto;
import usw.suwiki.domain.lecture.dto.LectureResponseDto;
import usw.suwiki.domain.lecture.entity.Lecture;
import usw.suwiki.domain.lecture.repository.LectureRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureRepository lectureRepository;

    @Transactional(readOnly = true)
    public LectureToJsonArray findAllLectureByFindOption(LectureFindOption lectureFindOption) {
        List<LectureResponseDto> dtoList = new ArrayList<>();
        LectureListAndCountDto dto = lectureRepository.findAllLectureByFindOption(lectureFindOption);
        for (Lecture lecture : dto.getLectureList()) {
            dtoList.add(new LectureResponseDto(lecture));
        }

        return new LectureToJsonArray(dtoList, dto.getCount());
    }

    @Transactional(readOnly = true)
    public LectureToJsonArray findAllLectureByMajorType(LectureFindOption lectureFindOption) {
        List<LectureResponseDto> dtoList = new ArrayList<>();
        LectureListAndCountDto dto = lectureRepository.findAllLectureByMajorType(lectureFindOption);
        for (Lecture lecture : dto.getLectureList()) {
            dtoList.add(new LectureResponseDto(lecture));
        }

        return new LectureToJsonArray(dtoList, dto.getCount());
    }

    @Transactional(readOnly = true)
    public LectureToJsonArray findLectureByFindOption(String searchValue, LectureFindOption lectureFindOption) {
        List<LectureResponseDto> dtoList = new ArrayList<>();
        LectureListAndCountDto dto = lectureRepository.findLectureByFindOption(searchValue, lectureFindOption);
        for (Lecture lecture : dto.getLectureList()) {
            dtoList.add(new LectureResponseDto(lecture));
        }

        return new LectureToJsonArray(dtoList, dto.getCount());
    }

    @Transactional(readOnly = true)
    public LectureToJsonArray findLectureByMajorType(String searchValue, LectureFindOption lectureFindOption) {
        List<LectureResponseDto> dtoList = new ArrayList<>();
        LectureListAndCountDto dto = lectureRepository.findLectureByMajorType(searchValue, lectureFindOption);
        for (Lecture lecture : dto.getLectureList()) {
            dtoList.add(new LectureResponseDto(lecture));
        }

        return new LectureToJsonArray(dtoList, dto.getCount());
    }

    @Transactional(readOnly = true)
    public LectureDetailResponseDto findByIdDetail(Long id) {
        Lecture lecture = lectureRepository.findById(id);
        LectureDetailResponseDto dto = new LectureDetailResponseDto(lecture);
        return dto;
    }

    @Transactional(readOnly = true)
    public List<String> findAllMajorType() {
        List<String> resultList = lectureRepository.findAllMajorType();
        return resultList;
    }

    public void updateLectureEvaluationIfCreateNewPost(EvaluatePostsToLecture post) {
        Lecture lecture = lectureRepository.findByIdPessimisticLock(post.getLectureId());
        lecture.handleLectureEvaluationIfNewPost(post);
    }

    public void updateLectureEvaluationIfUpdatePost(EvaluatePostsToLecture beforeUpdatePost, EvaluatePostsToLecture post) {
        Lecture lecture = lectureRepository.findByIdPessimisticLock(post.getLectureId());
        lecture.handleLectureEvaluationIfUpdatePost(beforeUpdatePost, post);
    }

    public void updateLectureEvaluationIfDeletePost(EvaluatePostsToLecture post) {
        Lecture lecture = lectureRepository.findByIdPessimisticLock(post.getLectureId());
        lecture.handleLectureEvaluationIfDeletePost(post);
    }

    public Lecture findById(Long id) {
        return lectureRepository.findById(id);
    }
}
