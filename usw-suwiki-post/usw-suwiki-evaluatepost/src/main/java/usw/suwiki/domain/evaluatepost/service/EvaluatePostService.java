package usw.suwiki.domain.evaluatepost.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.common.pagination.PageOption;
import usw.suwiki.core.exception.EvaluatePostException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.domain.evaluatepost.EvaluatePost;
import usw.suwiki.domain.evaluatepost.dto.EvaluatePostResponseByLectureIdDto;
import usw.suwiki.domain.evaluatepost.dto.EvaluatePostResponseByUserIdxDto;
import usw.suwiki.domain.evaluatepost.dto.EvaluatePostSaveDto;
import usw.suwiki.domain.evaluatepost.dto.EvaluatePostUpdateDto;
import usw.suwiki.domain.evaluatepost.dto.EvaluatePostsToLecture;
import usw.suwiki.domain.evaluatepost.dto.FindByLectureToJson;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.lecture.service.LectureCRUDService;
import usw.suwiki.domain.lecture.service.LectureService;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.service.UserCRUDService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluatePostService {
    private final EvaluatePostCRUDService evaluatePostCRUDService;
    private final LectureCRUDService lectureCRUDService;
    private final LectureService lectureService;
    private final UserCRUDService userCRUDService;

    @Transactional
    public void write(EvaluatePostSaveDto evaluatePostData, Long userIdx, Long lectureId) {
        checkAlreadyWrite(userIdx, lectureId);
        Lecture lecture = lectureService.findLectureById(lectureId);
        User user = userCRUDService.loadUserFromUserIdx(userIdx);
        EvaluatePost evaluatePost = createEvaluatePost(evaluatePostData, user, lecture);

        user.updateWritingEvaluatePost();
        EvaluatePostsToLecture lectureEvaluation = new EvaluatePostsToLecture(evaluatePost);
        updateLectureEvaluationIfCreateNewPost(lectureEvaluation);

        evaluatePostCRUDService.save(evaluatePost);
    }

    @Transactional
    public void update(Long evaluateIdx, EvaluatePostUpdateDto evaluatePostUpdateData) {
        EvaluatePost post = evaluatePostCRUDService.loadEvaluatePostFromEvaluatePostIdx(evaluateIdx);
        EvaluatePostsToLecture beforeUpdated = new EvaluatePostsToLecture(post);
        post.update(evaluatePostUpdateData);

        EvaluatePostsToLecture updated = new EvaluatePostsToLecture(post);
        updateLectureEvaluationIfUpdatePost(beforeUpdated, updated);
    }

    @Transactional(readOnly = true)
    public FindByLectureToJson readEvaluatePostsByLectureId(PageOption option, Long userIdx, Long lectureId) {
        List<EvaluatePostResponseByLectureIdDto> data = new ArrayList<>();

        List<EvaluatePost> evaluatePosts =
          evaluatePostCRUDService.loadEvaluatePostsFromLectureIdx(option, lectureId);

        for (EvaluatePost post : evaluatePosts) {
            data.add(new EvaluatePostResponseByLectureIdDto(post));
        }

        return setWrittenInformation(data, userIdx, lectureId);
    }

    private FindByLectureToJson setWrittenInformation(List<EvaluatePostResponseByLectureIdDto> data, Long userIdx, Long lectureId) {
        FindByLectureToJson response = new FindByLectureToJson(data);

        if (verifyIsUserCanWriteEvaluatePost(userIdx, lectureId)) {
            response.setWritten(Boolean.FALSE);
        }

        return response;
    }

    @Transactional(readOnly = true)
    public List<EvaluatePostResponseByUserIdxDto> readEvaluatePostsByUserId(PageOption option, Long userId) {
        List<EvaluatePostResponseByUserIdxDto> response = new ArrayList<>();

        List<EvaluatePost> evaluatePosts =
          evaluatePostCRUDService.loadEvaluatePostsFromUserIdxAndOption(option, userId);

        for (EvaluatePost post : evaluatePosts) {
            EvaluatePostResponseByUserIdxDto data = new EvaluatePostResponseByUserIdxDto(post);
            data.setSemesterList(post.getLecture().getSemester());
            response.add(data);
        }
        return response;
    }

    public boolean verifyIsUserCanWriteEvaluatePost(Long userIdx, Long lectureId) {
        Lecture lecture = lectureService.findLectureById(lectureId);
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
            throw new EvaluatePostException(ExceptionType.POSTS_WRITE_OVERLAP);
        }
    }

    private EvaluatePost createEvaluatePost(EvaluatePostSaveDto evaluatePostData, User user, Lecture lecture) {
        EvaluatePost evaluatePost = new EvaluatePost(evaluatePostData);
        evaluatePost.setUser(user);
        evaluatePost.setLecture(lecture);

        return evaluatePost;
    }
}
