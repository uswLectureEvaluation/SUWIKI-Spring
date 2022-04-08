package usw.suwiki.repository.evaluation;

import usw.suwiki.domain.exam.ExamPosts;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.user.User;
import usw.suwiki.dto.PageOption;
import usw.suwiki.domain.evaluation.EvaluatePosts;

import java.util.List;

public interface EvaluatePostsRepository{

    void save(EvaluatePosts EvaluatePosts);

    EvaluatePosts findById(Long id);

    List<EvaluatePosts> findByLectureId(PageOption option, Long lectureId);

    List<EvaluatePosts> findByUserId(PageOption option, Long userId);

    boolean verifyPostsByIdx(User user, Lecture lecture);

    void delete(EvaluatePosts evaluatePosts);

    List<EvaluatePosts> findAllByUserId(Long userId);
}
