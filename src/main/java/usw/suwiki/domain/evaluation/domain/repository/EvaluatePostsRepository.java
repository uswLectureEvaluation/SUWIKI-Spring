package usw.suwiki.domain.evaluation.domain.repository;

import usw.suwiki.domain.evaluation.domain.EvaluatePosts;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.global.PageOption;

import java.util.List;

public interface EvaluatePostsRepository {

    void save(EvaluatePosts EvaluatePosts);

    EvaluatePosts findById(Long id);

    List<EvaluatePosts> findByLectureId(PageOption option, Long lectureId);

    List<EvaluatePosts> findByUserId(PageOption option, Long userId);

    boolean isExistPostsByIdx(User user, Lecture lecture);

    void delete(EvaluatePosts evaluatePosts);

    List<EvaluatePosts> findAllByUserId(Long userId);
}
