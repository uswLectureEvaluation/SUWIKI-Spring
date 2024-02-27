package usw.suwiki.domain.evaluatepost.service;

import static usw.suwiki.global.exception.ExceptionType.EVALUATE_POST_NOT_FOUND;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.evaluatepost.domain.EvaluatePost;
import usw.suwiki.domain.evaluatepost.domain.repository.EvaluatePostRepository;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.service.UserCRUDService;
import usw.suwiki.global.PageOption;
import usw.suwiki.global.exception.errortype.EvaluatePostException;

@Service
@RequiredArgsConstructor
@Transactional
public class EvaluatePostCRUDService {

    private static final int LIMIT_PAGE_SIZE = 10;
    private final EvaluatePostRepository evaluatePostRepository;
    private final UserCRUDService userCRUDService;

    public void save(EvaluatePost evaluatePost) {
        evaluatePostRepository.save(evaluatePost);
    }

    public EvaluatePost loadEvaluatePostFromEvaluatePostIdx(Long evaluateIdx) {
        Optional<EvaluatePost> evaluatePost = evaluatePostRepository.findById(evaluateIdx);
        if (evaluatePost.isPresent()) {
            return evaluatePost.get();
        }
        throw new EvaluatePostException(EVALUATE_POST_NOT_FOUND);
    }

    public List<EvaluatePost> loadEvaluatePostsFromLectureIdx(
        PageOption option, Long lectureId
    ) {
        return evaluatePostRepository.findAllByLectureIdAndPageOption(
            lectureId,
            option.getOffset(),
            LIMIT_PAGE_SIZE
        );
    }

    public List<EvaluatePost> loadEvaluatePostsFromUserIdxAndOption(
        PageOption option,
        Long userId
    ) {
        return evaluatePostRepository.findByUserIdxAndPagePotion(
            userId,
            option.getOffset(),
            LIMIT_PAGE_SIZE
        );
    }

    public List<EvaluatePost> loadEvaluatePostsFromUserIdx(Long userId) {
        return evaluatePostRepository.findAllByUser(userCRUDService.loadUserFromUserIdx(userId));
    }

    public void deleteFromUserIdx(Long userIdx) {
        evaluatePostRepository.deleteAll(loadEvaluatePostsFromUserIdx(userIdx));
    }

    public void delete(EvaluatePost evaluatePost) {
        evaluatePostRepository.delete(evaluatePost);
    }

    public boolean isAlreadyWritten(User user, Lecture lecture) {
        return evaluatePostRepository.findByUserAndLecture(user, lecture).isEmpty();
    }
}
