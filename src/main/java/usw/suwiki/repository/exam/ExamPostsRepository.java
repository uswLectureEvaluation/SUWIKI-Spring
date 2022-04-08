package usw.suwiki.repository.exam;

import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.user.User;
import usw.suwiki.dto.PageOption;
import usw.suwiki.domain.exam.ExamPosts;

import java.util.List;

public interface ExamPostsRepository{
    void save(ExamPosts examPosts);

    ExamPosts findById(Long id);

    List<ExamPosts> findByLectureId(PageOption option, Long lectureId);

    List<ExamPosts> findByUserId(PageOption option, Long userId);

    boolean verifyPostsByIdx(User user, Lecture lecture);

    void delete(ExamPosts examPosts);

    List<ExamPosts> findAllByUserId(Long userId);
}
