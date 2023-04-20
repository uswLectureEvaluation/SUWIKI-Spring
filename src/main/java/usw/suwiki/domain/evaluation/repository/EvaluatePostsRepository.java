package usw.suwiki.domain.evaluation.repository;

import usw.suwiki.domain.evaluation.entity.EvaluatePosts;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.user.user.entity.User;
import usw.suwiki.global.PageOption;

import java.util.List;

public interface EvaluatePostsRepository {

    void save(EvaluatePosts EvaluatePosts);

    EvaluatePosts findById(Long id);

    List<EvaluatePosts> findByLectureId(PageOption option, Long lectureId);

    List<EvaluatePosts> findByUserId(PageOption option, Long userId);

    boolean verifyPostsByIdx(User user, Lecture lecture);

    void delete(EvaluatePosts evaluatePosts);

    List<EvaluatePosts> findAllByUserId(Long userId);
}
