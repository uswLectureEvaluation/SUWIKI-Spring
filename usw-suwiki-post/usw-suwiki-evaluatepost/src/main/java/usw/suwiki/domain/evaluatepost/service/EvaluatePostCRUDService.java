package usw.suwiki.domain.evaluatepost.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.common.pagination.PageOption;
import usw.suwiki.core.exception.EvaluatePostException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.domain.evaluatepost.EvaluatePost;
import usw.suwiki.domain.evaluatepost.EvaluatePostRepository;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.service.UserCRUDService;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EvaluatePostCRUDService {
    private static final int LIMIT_PAGE_SIZE = 10;

    private final EvaluatePostRepository evaluatePostRepository;
    private final UserCRUDService userCRUDService;

    @Transactional
    public void save(EvaluatePost evaluatePost) {
        evaluatePostRepository.save(evaluatePost);
    }

    public EvaluatePost loadEvaluatePostFromEvaluatePostIdx(Long evaluateIdx) {
        return evaluatePostRepository.findById(evaluateIdx)
          .orElseThrow(() -> new EvaluatePostException(ExceptionType.EVALUATE_POST_NOT_FOUND));
    }

    public List<EvaluatePost> loadEvaluatePostsFromLectureIdx(PageOption option, Long lectureId) {
        return evaluatePostRepository.findAllByLectureIdAndPageOption(lectureId, option.getOffset(), LIMIT_PAGE_SIZE);
    }

    public List<EvaluatePost> loadEvaluatePostsFromUserIdxAndOption(PageOption option, Long userId) {
        return evaluatePostRepository.findByUserIdxAndPagePotion(userId, option.getOffset(), LIMIT_PAGE_SIZE);
    }

    public List<EvaluatePost> loadEvaluatePostsFromUserIdx(Long userId) {
        return evaluatePostRepository.findAllByUser(userCRUDService.loadUserFromUserIdx(userId));
    }

    @Transactional
    public void deleteFromUserIdx(Long userIdx) {
        evaluatePostRepository.deleteAll(loadEvaluatePostsFromUserIdx(userIdx));
    }

    @Transactional
    public void delete(EvaluatePost evaluatePost) {
        evaluatePostRepository.delete(evaluatePost);
    }

    public boolean isAlreadyWritten(User user, Lecture lecture) {
        return evaluatePostRepository.findByUserAndLecture(user, lecture).isEmpty();
    }
}
