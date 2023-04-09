package usw.suwiki.domain.lecture.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import usw.suwiki.domain.evaluation.EvaluatePostsToLecture;
import usw.suwiki.domain.lecture.controller.dto.LectureDetailResponseDto;
import usw.suwiki.domain.lecture.controller.dto.LecturesAndCountDto;
import usw.suwiki.domain.lecture.controller.dto.LectureResponseDto;
import usw.suwiki.domain.lecture.controller.dto.LectureAndCountResponseForm;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.controller.dto.LectureFindOption;
import usw.suwiki.domain.lecture.repository.LectureRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureRepository lectureRepository;

    @Transactional(readOnly = true)
    public LectureAndCountResponseForm findLectureByKeyword(String keyword, LectureFindOption option) {
        if (option.majorTypeIsNull()) {
            return findLectureByKeywordAndOption(keyword, option);
        }
        return findLectureByKeywordAndMajorType(keyword, option);
    }

    @Transactional(readOnly = true)
    public LectureAndCountResponseForm findAllLecture(LectureFindOption option) {
        if (option.majorTypeIsNull()) {
            return findAllLectureByFindOption(option);
        }
        return findAllLectureByMajorType(option);
    }

    @Transactional(readOnly = true)
    public LectureDetailResponseDto findByIdDetail(Long id) {
        Lecture lecture = lectureRepository.findById(id);
        LectureDetailResponseDto response = new LectureDetailResponseDto(lecture);
        return response;
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

    private LectureAndCountResponseForm findLectureByKeywordAndOption(String keyword, LectureFindOption option) {
        LecturesAndCountDto lectureInfo = lectureRepository.findLectureByFindOption(keyword, option);
        return createLectureResponseForm(lectureInfo);
    }

    private LectureAndCountResponseForm findLectureByKeywordAndMajorType(String searchValue, LectureFindOption lectureFindOption) {
        LecturesAndCountDto lectureInfo = lectureRepository.findLectureByMajorType(searchValue, lectureFindOption);
        return createLectureResponseForm(lectureInfo);
    }

    private LectureAndCountResponseForm findAllLectureByFindOption(LectureFindOption lectureFindOption) {
        LecturesAndCountDto lectureInfo = lectureRepository.findAllLectureByFindOption(lectureFindOption);
        return createLectureResponseForm(lectureInfo);
    }

    private LectureAndCountResponseForm findAllLectureByMajorType(LectureFindOption lectureFindOption) {
        LecturesAndCountDto lectureInfo = lectureRepository.findAllLectureByMajorType(lectureFindOption);
        return createLectureResponseForm(lectureInfo);
    }

    private LectureAndCountResponseForm createLectureResponseForm(LecturesAndCountDto lectureInfo) {
        List<LectureResponseDto> dtoList = new ArrayList<>();
        for (Lecture lecture : lectureInfo.getLectureList()) {
            dtoList.add(new LectureResponseDto(lecture));
        }
        return new LectureAndCountResponseForm(dtoList, lectureInfo.getCount());
    }
}
