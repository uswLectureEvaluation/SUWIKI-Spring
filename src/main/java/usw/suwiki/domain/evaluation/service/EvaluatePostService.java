package usw.suwiki.domain.evaluation.service;

import static usw.suwiki.global.exception.ExceptionType.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.evaluation.service.dto.EvaluatePostsToLecture;
import usw.suwiki.domain.evaluation.controller.dto.EvaluatePostsSaveDto;
import usw.suwiki.domain.evaluation.controller.dto.EvaluatePostsUpdateDto;
import usw.suwiki.domain.evaluation.controller.dto.EvaluateResponseByLectureIdDto;
import usw.suwiki.domain.evaluation.controller.dto.EvaluateResponseByUserIdxDto;
import usw.suwiki.domain.evaluation.domain.EvaluatePost;
import usw.suwiki.domain.evaluation.service.dto.FindByLectureToJson;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.service.LectureCRUDService;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.service.UserCRUDService;
import usw.suwiki.global.PageOption;
import usw.suwiki.global.exception.errortype.EvaluatePostException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluatePostService {

    private final EvaluatePostCRUDService evaluatePostCRUDService;
    private final LectureCRUDService lectureCRUDService;
    private final UserCRUDService userCRUDService;

    @Transactional
    public void write(EvaluatePostsSaveDto evaluatePostData, Long userIdx, Long lectureId) {
        checkAlreadyWrite(userIdx, lectureId);
        Lecture lecture = lectureCRUDService.loadLectureFromId(lectureId);
        User user = userCRUDService.loadUserFromUserIdx(userIdx);
        EvaluatePost evaluatePost = createEvaluatePost(evaluatePostData, user, lecture);

        user.updateWritingEvaluatePost();
        EvaluatePostsToLecture lectureEvaluation = new EvaluatePostsToLecture(evaluatePost);
        updateLectureEvaluationIfCreateNewPost(lectureEvaluation);

        evaluatePostCRUDService.save(evaluatePost);
    }

    @Transactional
    public void update(Long evaluateIdx, EvaluatePostsUpdateDto evaluatePostUpdateData) {
        EvaluatePost post = evaluatePostCRUDService.loadEvaluatePostFromEvaluatePostIdx(evaluateIdx);
        EvaluatePostsToLecture beforeUpdated = new EvaluatePostsToLecture(post);
        post.update(evaluatePostUpdateData);

        EvaluatePostsToLecture updated = new EvaluatePostsToLecture(post);
        updateLectureEvaluationIfUpdatePost(beforeUpdated, updated);
    }

    @Transactional(readOnly = true)
    public FindByLectureToJson readEvaluatePostsByLectureId(
            PageOption option, Long userIdx, Long lectureId) {
        List<EvaluateResponseByLectureIdDto> data = new ArrayList<>();
        List<EvaluatePost> evaluatePosts = evaluatePostCRUDService.loadEvaluatePostsFromLectureIdx(option, lectureId);
        for (EvaluatePost post : evaluatePosts) {
            data.add(new EvaluateResponseByLectureIdDto(post));
        }

        FindByLectureToJson response = setWrittenInformation(data, userIdx, lectureId);
        return response;
    }

    private FindByLectureToJson setWrittenInformation(List<EvaluateResponseByLectureIdDto> data, Long userIdx,
        Long lectureId) {
        FindByLectureToJson response = new FindByLectureToJson(data);
        if (verifyIsUserCanWriteEvaluatePost(userIdx, lectureId)) {
            response.setWritten(Boolean.FALSE);
        }
        return response;
    }

    @Transactional(readOnly = true)
    public List<EvaluateResponseByUserIdxDto> readEvaluatePostsByUserId(PageOption option, Long userId) {
        List<EvaluateResponseByUserIdxDto> response = new ArrayList<>();
        List<EvaluatePost> evaluatePosts = evaluatePostCRUDService.loadEvaluatePostsFromUserIdxAndOption(option, userId);
        for (EvaluatePost post : evaluatePosts) {
            EvaluateResponseByUserIdxDto data = new EvaluateResponseByUserIdxDto(post);
            data.setSemesterList(post.getLecture().getSemester());
            response.add(data);
        }
        return response;
    }

    public boolean verifyIsUserCanWriteEvaluatePost(Long userIdx, Long lectureId) {
        Lecture lecture = lectureCRUDService.loadLectureFromId(lectureId);
        User user = userCRUDService.loadUserFromUserIdx(userIdx);
        return evaluatePostCRUDService.isAlreadyWritten(user, lecture);
    }

    public void executeDeleteEvaluatePost(Long evaluateIdx, Long userIdx) {
        EvaluatePost evaluatePost = evaluatePostCRUDService.loadEvaluatePostFromEvaluatePostIdx(evaluateIdx);
        User user = userCRUDService.loadUserFromUserIdx(userIdx);
        user.decreasePointAndWrittenEvaluationByDeleteEvaluatePosts();

        evaluatePostCRUDService.delete(evaluatePost);
    }

    public void updateLectureEvaluationIfCreateNewPost(EvaluatePostsToLecture post) {
        Lecture lecture = lectureCRUDService.loadLectureFromIdPessimisticLock(post.getLectureId());
        lecture.handleLectureEvaluationIfNewPost(post);
    }

    public void updateLectureEvaluationIfUpdatePost(EvaluatePostsToLecture beforeUpdatePost, EvaluatePostsToLecture post) {
        Lecture lecture = lectureCRUDService.loadLectureFromIdPessimisticLock(post.getLectureId());
        lecture.handleLectureEvaluationIfUpdatePost(beforeUpdatePost, post);
    }

    public void updateLectureEvaluationIfDeletePost(EvaluatePostsToLecture post) {
        Lecture lecture = lectureCRUDService.loadLectureFromIdPessimisticLock(post.getLectureId());
        lecture.handleLectureEvaluationIfDeletePost(post);
    }

    private void checkAlreadyWrite(Long userIdx, Long lectureIdx) {
        if (!(verifyIsUserCanWriteEvaluatePost(userIdx, lectureIdx))) {
            throw new EvaluatePostException(POSTS_WRITE_OVERLAP);
        }
    }

    private EvaluatePost createEvaluatePost(EvaluatePostsSaveDto evaluatePostData, User user, Lecture lecture) {
        EvaluatePost evaluatePost = new EvaluatePost(evaluatePostData);
        evaluatePost.setUser(user);
        evaluatePost.setLecture(lecture);

        return evaluatePost;
    }
}
