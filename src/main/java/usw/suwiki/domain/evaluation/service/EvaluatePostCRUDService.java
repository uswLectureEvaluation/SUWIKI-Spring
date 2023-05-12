package usw.suwiki.domain.evaluation.service;

import static usw.suwiki.global.exception.ExceptionType.*;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import usw.suwiki.domain.evaluation.domain.EvaluatePosts;
import usw.suwiki.domain.evaluation.domain.repository.EvaluatePostsRepository;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.global.PageOption;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.exception.errortype.EvaluatePostException;

@Service
@RequiredArgsConstructor
public class EvaluatePostCRUDService {

    private final EvaluatePostsRepository evaluatePostsRepository;

    public void save(EvaluatePosts evaluatePost) {
        evaluatePostsRepository.save(evaluatePost);
    }

    public EvaluatePosts loadEvaluatePostFromEvaluatePostIdx(Long evaluateIdx) {
        EvaluatePosts evaluatePost = evaluatePostsRepository.findById(evaluateIdx);
        validateNotNull(evaluatePost);

        return evaluatePost;
    }

    public List<EvaluatePosts> loadEvaluatePostsFromLectureIdx(
            PageOption option, Long lectureId) {
        return evaluatePostsRepository.findByLectureId(option, lectureId);
    }

    public List<EvaluatePosts> loadEvaluatePostsFromUserIdxAndOption(PageOption option, Long userId) {
        return evaluatePostsRepository.findByUserId(option, userId);
    }

    public List<EvaluatePosts> loadEvaluatePostsFromUserIdx(Long userId) {
        return evaluatePostsRepository.findAllByUserId(userId);
    }


    public void deleteFromUserIdx(Long userIdx) {
        List<EvaluatePosts> evaluatePosts = loadEvaluatePostsFromUserIdx(userIdx);
        for (EvaluatePosts evaluatePost : evaluatePosts) {
            evaluatePostsRepository.delete(evaluatePost);
        }
    }

    public void delete(EvaluatePosts evaluatePost) {
        evaluatePostsRepository.delete(evaluatePost);
    }

    public boolean verifyIsUserCanWriteEvaluatePost(User user, Lecture lecture) {
        return !(evaluatePostsRepository.isExistPostsByIdx(user, lecture));
    }

    public void validateNotNull(EvaluatePosts evaluatePost) {
        if (evaluatePost == null) {
            throw new EvaluatePostException(EVALUATE_POST_NOT_FOUND);
        }
    }
}
