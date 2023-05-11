package usw.suwiki.domain.exam.service;

import static usw.suwiki.global.exception.ExceptionType.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import usw.suwiki.domain.exam.controller.dto.ExamPostsSaveDto;
import usw.suwiki.domain.exam.controller.dto.ExamPostsUpdateDto;
import usw.suwiki.domain.exam.controller.dto.ExamResponseByLectureIdDto;
import usw.suwiki.domain.exam.controller.dto.ExamResponseByUserIdxDto;
import usw.suwiki.domain.exam.controller.dto.ReadExamPostResponse;
import usw.suwiki.domain.exam.domain.ExamPosts;
import usw.suwiki.domain.exam.domain.repository.ExamPostsRepository;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.service.LectureCRUDService;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.service.UserCRUDService;
import usw.suwiki.global.PageOption;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.exception.errortype.ExamPostException;
import usw.suwiki.global.exception.errortype.LectureException;

@Service
@RequiredArgsConstructor
public class ExamPostCRUDService {
    private final ExamPostsRepository examPostsRepository;

    public void save(ExamPosts examPost) {
        examPostsRepository.save(examPost);
    }

    public ExamPosts loadExamPostFromExamPostIdx(Long examIdx) {
        ExamPosts examPost = examPostsRepository.findById(examIdx);
        validateNotNull(examPost);

        return examPost;
    }

    public void update(Long examIdx, ExamPostsUpdateDto dto) {
        ExamPosts posts = examPostsRepository.findById(examIdx);
        posts.update(dto);
    }

    public  List<ExamPosts> loadExamPostsFromLectureIdx(Long lectureIdx, PageOption option) {
        return examPostsRepository.findByLectureId(option, lectureIdx);
    }

    public List<ExamPosts> loadExamPostsFromUserIdxAndPageOption(Long userIdx, PageOption option) {
        return examPostsRepository.findByUserId(option, userIdx);
    }

    public boolean isWrite(User user, Lecture lecture) {
        return examPostsRepository.isWrite(user, lecture);
    }

    public List<ExamPosts> loadExamPostsFromUserIdx(Long userIdx) {
       return examPostsRepository.findAllByUserId(userIdx);
    }

    public void delete(ExamPosts examPost) {
        examPostsRepository.delete(examPost);
    }

    public void validateNotNull(ExamPosts examPost) {
        if (examPost == null) {
            throw new ExamPostException(EXAM_POST_NOT_FOUND);
        }
    }
}
